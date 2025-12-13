# 開発ワークフロー

Yoin（余韻）プロジェクトでの機能開発フローを、ケース別に詳しく説明します。

---

## 🔄 機能開発の基本フロー

### 標準的な開発フロー（推奨）

```
1. 要件整理・設計
   ↓
2. Domain層（ビジネスロジック）
   ↓
3. Data層（データ取得・保存）
   ↓
4. Presentation層（UI・ViewModel）
   ↓
5. プラットフォーム固有実装（必要に応じて）
   ↓
6. テスト作成
   ↓
7. 動作確認・レビュー
```

**重要な原則**:
- **ボトムアップ開発**: Domain → Data → Presentation の順で実装
- **Clean Architecture 遵守**: Domain層は他のレイヤーに依存しない
- **MVI Pattern**: Intent → State の単方向データフロー
- **テスト駆動**: 主要なUseCaseとRepositoryはテストを書く

---

## 📱 ケース別開発フロー

### ケース1: 新しい画面を追加する場合

**例**: 「ルーム作成画面」を作る

#### Step 1: Domain層の定義

まず、ビジネスロジックを定義します。

**1-1. モデルの定義**

```kotlin
// shared/src/commonMain/kotlin/com/yoin/domain/model/Room.kt
package com.yoin.domain.model

import kotlinx.datetime.LocalDate

data class Room(
    val id: String,
    val name: String,
    val destination: String? = null,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: RoomStatus = RoomStatus.UPCOMING,
    val ownerId: String,
    val createdAt: Instant = Clock.System.now()
)

enum class RoomStatus {
    UPCOMING,
    ACTIVE,
    PENDING_DEVELOPMENT,
    DEVELOPED,
    ARCHIVED
}
```

**1-2. Repository インターフェースの定義**

```kotlin
// shared/src/commonMain/kotlin/com/yoin/domain/repository/RoomRepository.kt
package com.yoin.domain.repository

import com.yoin.domain.model.Room

interface RoomRepository {
    suspend fun createRoom(room: Room): Result<Room>
    suspend fun getRooms(userId: String): List<Room>
    suspend fun getRoomById(roomId: String): Room?
    suspend fun updateRoom(room: Room): Result<Unit>
    suspend fun deleteRoom(roomId: String): Result<Unit>
}
```

**1-3. UseCase の作成**

```kotlin
// shared/src/commonMain/kotlin/com/yoin/domain/usecase/CreateRoomUseCase.kt
package com.yoin.domain.usecase

import com.yoin.domain.model.Room
import com.yoin.domain.repository.RoomRepository
import kotlinx.datetime.LocalDate
import java.util.UUID

class CreateRoomUseCase(
    private val repository: RoomRepository,
    private val getCurrentUserId: () -> String // DI経由で取得
) {
    suspend operator fun invoke(
        name: String,
        destination: String?,
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<Room> {
        // バリデーション
        if (name.isBlank()) {
            return Result.Error(InvalidInputException("名前を入力してください"))
        }

        if (name.length > 100) {
            return Result.Error(InvalidInputException("名前は100文字以内にしてください"))
        }

        if (startDate > endDate) {
            return Result.Error(InvalidInputException("開始日は終了日より前にしてください"))
        }

        // Roomオブジェクトの生成
        val room = Room(
            id = UUID.randomUUID().toString(),
            name = name,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            ownerId = getCurrentUserId()
        )

        // Repository経由で保存
        return repository.createRoom(room)
    }
}
```

---

#### Step 2: Data層の実装

**2-1. DTO（Data Transfer Object）の定義**

```kotlin
// shared/src/commonMain/kotlin/com/yoin/data/dto/RoomDto.kt
package com.yoin.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoomDto(
    val id: String,
    val name: String,
    val destination: String? = null,
    @SerialName("start_date") val startDate: String, // ISO 8601 format
    @SerialName("end_date") val endDate: String,
    val status: String,
    @SerialName("owner_id") val ownerId: String,
    @SerialName("created_at") val createdAt: String
)
```

**2-2. Mapper（DTO ⇔ Domain Model）**

```kotlin
// shared/src/commonMain/kotlin/com/yoin/data/mapper/RoomMapper.kt
package com.yoin.data.mapper

import com.yoin.data.dto.RoomDto
import com.yoin.domain.model.Room
import com.yoin.domain.model.RoomStatus
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

fun RoomDto.toDomain(): Room {
    return Room(
        id = id,
        name = name,
        destination = destination,
        startDate = LocalDate.parse(startDate),
        endDate = LocalDate.parse(endDate),
        status = RoomStatus.valueOf(status.uppercase()),
        ownerId = ownerId,
        createdAt = Instant.parse(createdAt)
    )
}

fun Room.toDto(): RoomDto {
    return RoomDto(
        id = id,
        name = name,
        destination = destination,
        startDate = startDate.toString(),
        endDate = endDate.toString(),
        status = status.name.lowercase(),
        ownerId = ownerId,
        createdAt = createdAt.toString()
    )
}
```

**2-3. Repository の実装**

```kotlin
// shared/src/commonMain/kotlin/com/yoin/data/repository/RoomRepositoryImpl.kt
package com.yoin.data.repository

import com.yoin.data.dto.RoomDto
import com.yoin.data.mapper.toDomain
import com.yoin.data.mapper.toDto
import com.yoin.domain.model.Room
import com.yoin.domain.repository.RoomRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class RoomRepositoryImpl(
    private val supabase: SupabaseClient
) : RoomRepository {

    override suspend fun createRoom(room: Room): Result<Room> {
        return try {
            val created = supabase.from("rooms")
                .insert(room.toDto())
                .decodeSingle<RoomDto>()

            Result.Success(created.toDomain())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getRooms(userId: String): List<Room> {
        return try {
            supabase.from("rooms")
                .select {
                    filter {
                        or {
                            eq("owner_id", userId)
                            // または room_members 経由
                        }
                    }
                    order("created_at", Order.DESCENDING)
                }
                .decodeList<RoomDto>()
                .map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRoomById(roomId: String): Room? {
        return try {
            supabase.from("rooms")
                .select {
                    filter { eq("id", roomId) }
                }
                .decodeSingle<RoomDto>()
                .toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateRoom(room: Room): Result<Unit> {
        return try {
            supabase.from("rooms")
                .update(room.toDto()) {
                    filter { eq("id", room.id) }
                }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteRoom(roomId: String): Result<Unit> {
        return try {
            supabase.from("rooms")
                .delete {
                    filter { eq("id", roomId) }
                }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

---

#### Step 3: Presentation層（MVI）

**3-1. Intent（ユーザーアクション）**

```kotlin
// composeApp/src/commonMain/kotlin/ui/screens/room/create/CreateRoomIntent.kt
package com.yoin.ui.screens.room.create

import kotlinx.datetime.LocalDate

sealed interface CreateRoomIntent {
    data class UpdateName(val name: String) : CreateRoomIntent
    data class UpdateDestination(val destination: String) : CreateRoomIntent
    data class UpdateStartDate(val date: LocalDate) : CreateRoomIntent
    data class UpdateEndDate(val date: LocalDate) : CreateRoomIntent
    object CreateRoom : CreateRoomIntent
    object ClearError : CreateRoomIntent
}
```

**3-2. State（UI状態）**

```kotlin
// composeApp/src/commonMain/kotlin/ui/screens/room/create/CreateRoomState.kt
package com.yoin.ui.screens.room.create

import kotlinx.datetime.LocalDate

data class CreateRoomState(
    val name: String = "",
    val destination: String = "",
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isFormValid: Boolean
        get() = name.isNotBlank() && startDate != null && endDate != null
}
```

**3-3. Effect（一時的なイベント）**

```kotlin
// composeApp/src/commonMain/kotlin/ui/screens/room/create/CreateRoomEffect.kt
package com.yoin.ui.screens.room.create

sealed interface CreateRoomEffect {
    data class ShowToast(val message: String) : CreateRoomEffect
    object NavigateBack : CreateRoomEffect
}
```

**3-4. ViewModel**

```kotlin
// composeApp/src/commonMain/kotlin/ui/screens/room/create/CreateRoomViewModel.kt
package com.yoin.ui.screens.room.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yoin.domain.usecase.CreateRoomUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CreateRoomViewModel(
    private val createRoomUseCase: CreateRoomUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CreateRoomState())
    val state: StateFlow<CreateRoomState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<CreateRoomEffect>()
    val effect: SharedFlow<CreateRoomEffect> = _effect.asSharedFlow()

    fun onIntent(intent: CreateRoomIntent) {
        when (intent) {
            is CreateRoomIntent.UpdateName -> {
                _state.update { it.copy(name = intent.name, error = null) }
            }
            is CreateRoomIntent.UpdateDestination -> {
                _state.update { it.copy(destination = intent.destination) }
            }
            is CreateRoomIntent.UpdateStartDate -> {
                _state.update { it.copy(startDate = intent.date, error = null) }
            }
            is CreateRoomIntent.UpdateEndDate -> {
                _state.update { it.copy(endDate = intent.date, error = null) }
            }
            is CreateRoomIntent.CreateRoom -> createRoom()
            is CreateRoomIntent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun createRoom() {
        viewModelScope.launch {
            val currentState = _state.value

            if (!currentState.isFormValid) {
                _state.update { it.copy(error = "入力内容を確認してください") }
                return@launch
            }

            _state.update { it.copy(isLoading = true, error = null) }

            val result = createRoomUseCase(
                name = currentState.name,
                destination = currentState.destination.ifBlank { null },
                startDate = currentState.startDate!!,
                endDate = currentState.endDate!!
            )

            when (result) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.emit(CreateRoomEffect.ShowToast("ルームを作成しました"))
                    _effect.emit(CreateRoomEffect.NavigateBack)
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = result.exception.message ?: "エラーが発生しました"
                        )
                    }
                }
            }
        }
    }
}
```

---

#### Step 4: UI（Compose）

```kotlin
// composeApp/src/commonMain/kotlin/ui/screens/room/create/CreateRoomScreen.kt
package com.yoin.ui.screens.room.create

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoomScreen(
    viewModel: CreateRoomViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    // Effect の購読（一時的なイベント）
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CreateRoomEffect.ShowToast -> {
                    // トースト表示（プラットフォーム固有実装）
                }
                is CreateRoomEffect.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ルーム作成") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // エラー表示
            if (state.error != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = state.error!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // 名前入力
            OutlinedTextField(
                value = state.name,
                onValueChange = {
                    viewModel.onIntent(CreateRoomIntent.UpdateName(it))
                },
                label = { Text("旅行名 *") },
                placeholder = { Text("例: 北海道旅行") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                isError = state.error != null && state.name.isBlank(),
                singleLine = true
            )

            // 目的地入力（オプション）
            OutlinedTextField(
                value = state.destination,
                onValueChange = {
                    viewModel.onIntent(CreateRoomIntent.UpdateDestination(it))
                },
                label = { Text("目的地") },
                placeholder = { Text("例: 北海道") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                singleLine = true
            )

            // 開始日選択
            DatePickerField(
                label = "開始日 *",
                selectedDate = state.startDate,
                onDateSelected = {
                    viewModel.onIntent(CreateRoomIntent.UpdateStartDate(it))
                },
                enabled = !state.isLoading
            )

            // 終了日選択
            DatePickerField(
                label = "終了日 *",
                selectedDate = state.endDate,
                onDateSelected = {
                    viewModel.onIntent(CreateRoomIntent.UpdateEndDate(it))
                },
                enabled = !state.isLoading,
                minDate = state.startDate
            )

            Spacer(modifier = Modifier.weight(1f))

            // 作成ボタン
            Button(
                onClick = {
                    viewModel.onIntent(CreateRoomIntent.CreateRoom)
                },
                enabled = !state.isLoading && state.isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("作成")
                }
            }
        }
    }
}

@Composable
private fun DatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    enabled: Boolean = true,
    minDate: LocalDate? = null,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate?.toString() ?: "",
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { showDatePicker = true }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "日付選択")
            }
        }
    )

    if (showDatePicker) {
        // DatePickerDialog（実装省略）
        // プラットフォーム固有のDatePickerを表示
    }
}
```

---

#### Step 5: DI設定

```kotlin
// shared/src/commonMain/kotlin/di/AppModule.kt
package com.yoin.di

import com.yoin.data.repository.RoomRepositoryImpl
import com.yoin.domain.repository.RoomRepository
import com.yoin.domain.usecase.CreateRoomUseCase
import com.yoin.ui.screens.room.create.CreateRoomViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Repository
    single<RoomRepository> { RoomRepositoryImpl(get()) }

    // UseCase
    factory { CreateRoomUseCase(get(), get()) }

    // ViewModel
    viewModel { CreateRoomViewModel(get()) }
}
```

---

#### Step 6: Navigation

```kotlin
// composeApp/src/commonMain/kotlin/navigation/AppNavigation.kt
package com.yoin.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.yoin.ui.screens.room.create.CreateRoomScreen
import com.yoin.ui.screens.room.create.CreateRoomViewModel
import org.koin.compose.koinInject

fun NavGraphBuilder.roomNavigation(navController: NavHostController) {
    composable("room/create") {
        val viewModel: CreateRoomViewModel = koinInject()
        CreateRoomScreen(
            viewModel = viewModel,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}
```

---

#### Step 7: テスト

**7-1. UseCase のテスト**

```kotlin
// shared/src/commonTest/kotlin/domain/usecase/CreateRoomUseCaseTest.kt
package com.yoin.domain.usecase

import com.yoin.domain.model.Room
import com.yoin.domain.repository.RoomRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.*

class CreateRoomUseCaseTest {

    private val repository = mockk<RoomRepository>()
    private val getCurrentUserId = mockk<() -> String>()
    private lateinit var useCase: CreateRoomUseCase

    @BeforeTest
    fun setup() {
        useCase = CreateRoomUseCase(repository, getCurrentUserId)
        every { getCurrentUserId() } returns "user123"
    }

    @AfterTest
    fun teardown() {
        clearAllMocks()
    }

    @Test
    fun `should create room when input is valid`() = runTest {
        // Given
        val name = "北海道旅行"
        val destination = "北海道"
        val startDate = LocalDate(2025, 7, 1)
        val endDate = LocalDate(2025, 7, 5)

        val mockRoom = Room(
            id = "room123",
            name = name,
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            ownerId = "user123"
        )

        coEvery { repository.createRoom(any()) } returns Result.Success(mockRoom)

        // When
        val result = useCase(name, destination, startDate, endDate)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(mockRoom, (result as Result.Success).data)
        coVerify(exactly = 1) { repository.createRoom(any()) }
    }

    @Test
    fun `should return error when name is blank`() = runTest {
        // When
        val result = useCase("", null, LocalDate(2025, 7, 1), LocalDate(2025, 7, 5))

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is InvalidInputException)
        coVerify(exactly = 0) { repository.createRoom(any()) }
    }

    @Test
    fun `should return error when name exceeds max length`() = runTest {
        // Given
        val longName = "a".repeat(101)

        // When
        val result = useCase(longName, null, LocalDate(2025, 7, 1), LocalDate(2025, 7, 5))

        // Then
        assertTrue(result is Result.Error)
    }

    @Test
    fun `should return error when start date is after end date`() = runTest {
        // When
        val result = useCase(
            "北海道旅行",
            null,
            LocalDate(2025, 7, 10),
            LocalDate(2025, 7, 5)
        )

        // Then
        assertTrue(result is Result.Error)
        coVerify(exactly = 0) { repository.createRoom(any()) }
    }
}
```

**7-2. ViewModel のテスト**

```kotlin
// composeApp/src/commonTest/kotlin/ui/screens/room/create/CreateRoomViewModelTest.kt
package com.yoin.ui.screens.room.create

import app.cash.turbine.test
import com.yoin.domain.model.Room
import com.yoin.domain.usecase.CreateRoomUseCase
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlinx.datetime.LocalDate
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class CreateRoomViewModelTest {

    private val createRoomUseCase = mockk<CreateRoomUseCase>()
    private lateinit var viewModel: CreateRoomViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CreateRoomViewModel(createRoomUseCase)
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `should update name when UpdateName intent is sent`() = runTest {
        // When
        viewModel.onIntent(CreateRoomIntent.UpdateName("北海道旅行"))

        // Then
        assertEquals("北海道旅行", viewModel.state.value.name)
    }

    @Test
    fun `should update dates when UpdateStartDate and UpdateEndDate intents are sent`() = runTest {
        // When
        val startDate = LocalDate(2025, 7, 1)
        val endDate = LocalDate(2025, 7, 5)

        viewModel.onIntent(CreateRoomIntent.UpdateStartDate(startDate))
        viewModel.onIntent(CreateRoomIntent.UpdateEndDate(endDate))

        // Then
        assertEquals(startDate, viewModel.state.value.startDate)
        assertEquals(endDate, viewModel.state.value.endDate)
    }

    @Test
    fun `should create room successfully when CreateRoom intent is sent`() = runTest {
        // Given
        viewModel.onIntent(CreateRoomIntent.UpdateName("北海道旅行"))
        viewModel.onIntent(CreateRoomIntent.UpdateStartDate(LocalDate(2025, 7, 1)))
        viewModel.onIntent(CreateRoomIntent.UpdateEndDate(LocalDate(2025, 7, 5)))

        val mockRoom = mockk<Room>()
        coEvery { createRoomUseCase(any(), any(), any(), any()) } returns Result.Success(mockRoom)

        // When
        viewModel.effect.test {
            viewModel.onIntent(CreateRoomIntent.CreateRoom)
            advanceUntilIdle()

            // Then
            val toast = awaitItem()
            assertTrue(toast is CreateRoomEffect.ShowToast)

            val navigate = awaitItem()
            assertTrue(navigate is CreateRoomEffect.NavigateBack)
        }

        assertFalse(viewModel.state.value.isLoading)
        coVerify(exactly = 1) { createRoomUseCase(any(), any(), any(), any()) }
    }

    @Test
    fun `should show error when CreateRoom fails`() = runTest {
        // Given
        viewModel.onIntent(CreateRoomIntent.UpdateName("北海道旅行"))
        viewModel.onIntent(CreateRoomIntent.UpdateStartDate(LocalDate(2025, 7, 1)))
        viewModel.onIntent(CreateRoomIntent.UpdateEndDate(LocalDate(2025, 7, 5)))

        coEvery { createRoomUseCase(any(), any(), any(), any()) } returns
            Result.Error(Exception("ネットワークエラー"))

        // When
        viewModel.onIntent(CreateRoomIntent.CreateRoom)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.state.value.isLoading)
        assertNotNull(viewModel.state.value.error)
        assertTrue(viewModel.state.value.error!!.contains("ネットワークエラー"))
    }
}
```

---

### ケース2: Android/iOS固有の機能を作る場合

**例**: 「カメラ機能」を実装

#### Step 1: commonMain で expect を定義

```kotlin
// shared/src/commonMain/kotlin/com/yoin/platform/camera/CameraService.kt
package com.yoin.platform.camera

/**
 * カメラサービス
 *
 * プラットフォーム固有のカメラ機能を抽象化
 */
expect class CameraService {
    /**
     * カメラ権限をリクエスト
     * @return 権限が付与されたかどうか
     */
    suspend fun requestPermission(): Boolean

    /**
     * カメラを起動して写真を撮影
     * @return 撮影した画像のバイト配列、キャンセル時は null
     */
    suspend fun takePicture(): ByteArray?

    /**
     * カメラが利用可能かどうか
     */
    fun isCameraAvailable(): Boolean
}
```

---

#### Step 2: Android で actual を実装

```kotlin
// shared/src/androidMain/kotlin/com/yoin/platform/camera/CameraService.kt
package com.yoin.platform.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.coroutines.resume

actual class CameraService(
    private val context: Context
) {
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    actual suspend fun requestPermission(): Boolean {
        return withContext(Dispatchers.Main) {
            val permission = Manifest.permission.CAMERA

            if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                // 権限リクエスト
                // ActivityResultContract を使用する必要があるため、
                // 実際にはActivityから呼び出す
                suspendCancellableCoroutine { continuation ->
                    // 権限リクエスト処理
                    // Activity経由で実装
                }
            }
        }
    }

    actual suspend fun takePicture(): ByteArray? {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val imageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build()

                    try {
                        imageCapture.takePicture(
                            cameraExecutor,
                            object : ImageCapture.OnImageCapturedCallback() {
                                override fun onCaptureSuccess(image: ImageProxy) {
                                    val buffer = image.planes[0].buffer
                                    val bytes = ByteArray(buffer.remaining())
                                    buffer.get(bytes)
                                    image.close()
                                    continuation.resume(bytes)
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    continuation.resume(null)
                                }
                            }
                        )
                    } catch (e: Exception) {
                        continuation.resume(null)
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        }
    }

    actual fun isCameraAvailable(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }
}
```

---

#### Step 3: iOS で actual を実装

```kotlin
// shared/src/iosMain/kotlin/com/yoin/platform/camera/CameraService.kt
package com.yoin.platform.camera

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.*
import platform.UIKit.UIImageJPEGRepresentation
import kotlin.coroutines.resume

actual class CameraService {

    actual suspend fun requestPermission(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)

            when (status) {
                AVAuthorizationStatusAuthorized -> {
                    continuation.resume(true)
                }
                AVAuthorizationStatusNotDetermined -> {
                    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                        continuation.resume(granted)
                    }
                }
                else -> {
                    continuation.resume(false)
                }
            }
        }
    }

    actual suspend fun takePicture(): ByteArray? {
        return suspendCancellableCoroutine { continuation ->
            // AVFoundation で撮影
            val captureSession = AVCaptureSession()

            val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
            if (device == null) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            try {
                val input = AVCaptureDeviceInput.deviceInputWithDevice(device, null)
                captureSession.addInput(input)

                val output = AVCapturePhotoOutput()
                captureSession.addOutput(output)

                captureSession.startRunning()

                val settings = AVCapturePhotoSettings()
                output.capturePhotoWithSettings(settings, object : AVCapturePhotoCaptureDelegateProtocol {
                    override fun captureOutput(
                        output: AVCapturePhotoOutput,
                        didFinishProcessingPhoto: AVCapturePhoto,
                        error: NSError?
                    ) {
                        if (error != null) {
                            continuation.resume(null)
                            return
                        }

                        val imageData = didFinishProcessingPhoto.fileDataRepresentation()
                        val bytes = imageData?.bytes?.let {
                            ByteArray(imageData.length.toInt()).also { array ->
                                imageData.getBytes(array, imageData.length)
                            }
                        }

                        captureSession.stopRunning()
                        continuation.resume(bytes)
                    }
                })
            } catch (e: Exception) {
                continuation.resume(null)
            }
        }
    }

    actual fun isCameraAvailable(): Boolean {
        return AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo) != null
    }
}
```

---

#### Step 4: UseCase で使用

```kotlin
// shared/src/commonMain/kotlin/com/yoin/domain/usecase/TakePhotoUseCase.kt
package com.yoin.domain.usecase

import com.yoin.domain.model.Photo
import com.yoin.domain.repository.PhotoRepository
import com.yoin.platform.camera.CameraService

class TakePhotoUseCase(
    private val cameraService: CameraService,
    private val photoRepository: PhotoRepository,
    private val imageProcessor: ImageProcessor
) {
    suspend operator fun invoke(
        roomId: String,
        filter: Filter
    ): Result<Photo> {
        // カメラが利用可能か確認
        if (!cameraService.isCameraAvailable()) {
            return Result.Error(CameraException("カメラが利用できません"))
        }

        // 権限チェック
        val hasPermission = cameraService.requestPermission()
        if (!hasPermission) {
            return Result.Error(PermissionDeniedException("カメラの権限が必要です"))
        }

        // 撮影
        val imageBytes = cameraService.takePicture()
            ?: return Result.Error(CameraException("撮影に失敗しました"))

        // フィルター適用
        val processedImage = imageProcessor.applyFilter(imageBytes, filter)

        // 日付スタンプ追加
        val stampedImage = imageProcessor.addDateStamp(processedImage)

        // アップロード
        return photoRepository.uploadPhoto(roomId, stampedImage)
    }
}
```

---

### ケース3: データベーステーブルを追加する場合

**例**: 「通知設定」テーブルを追加

#### Step 1: Supabase マイグレーション

```sql
-- supabase/migrations/002_add_notification_settings.sql

-- ============================================
-- notification_settings（通知設定）
-- ============================================
CREATE TABLE notification_settings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
  push_enabled BOOLEAN NOT NULL DEFAULT TRUE,
  room_invite BOOLEAN NOT NULL DEFAULT TRUE,
  member_joined BOOLEAN NOT NULL DEFAULT TRUE,
  development_complete BOOLEAN NOT NULL DEFAULT TRUE,
  trip_reminder BOOLEAN NOT NULL DEFAULT TRUE,
  photo_returned BOOLEAN NOT NULL DEFAULT TRUE,
  email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
  marketing BOOLEAN NOT NULL DEFAULT FALSE,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Index
CREATE INDEX idx_notification_settings_user ON notification_settings(user_id);

-- RLS（Row Level Security）
ALTER TABLE notification_settings ENABLE ROW LEVEL SECURITY;

-- ユーザーは自分の設定のみ閲覧可能
CREATE POLICY "Users can view own settings"
ON notification_settings FOR SELECT
USING (user_id = auth.uid());

-- ユーザーは自分の設定のみ更新可能
CREATE POLICY "Users can update own settings"
ON notification_settings FOR UPDATE
USING (user_id = auth.uid());

-- ユーザーは自分の設定のみ作成可能
CREATE POLICY "Users can insert own settings"
ON notification_settings FOR INSERT
WITH CHECK (user_id = auth.uid());

-- Comment
COMMENT ON TABLE notification_settings IS 'ユーザーごとの通知設定';
COMMENT ON COLUMN notification_settings.push_enabled IS 'プッシュ通知の有効/無効';
COMMENT ON COLUMN notification_settings.marketing IS 'マーケティング通知の受信可否';

-- デフォルト設定を作成するトリガー
CREATE OR REPLACE FUNCTION create_default_notification_settings()
RETURNS TRIGGER AS $$
BEGIN
  INSERT INTO notification_settings (user_id)
  VALUES (NEW.id);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_create_default_notification_settings
AFTER INSERT ON users
FOR EACH ROW
EXECUTE FUNCTION create_default_notification_settings();
```

---

#### Step 2: Domain Model

```kotlin
// shared/src/commonMain/kotlin/com/yoin/domain/model/NotificationSettings.kt
package com.yoin.domain.model

data class NotificationSettings(
    val id: String,
    val userId: String,
    val pushEnabled: Boolean,
    val roomInvite: Boolean,
    val memberJoined: Boolean,
    val developmentComplete: Boolean,
    val tripReminder: Boolean,
    val photoReturned: Boolean,
    val emailEnabled: Boolean,
    val marketing: Boolean
)
```

---

#### Step 3: Repository

```kotlin
// shared/src/commonMain/kotlin/com/yoin/domain/repository/NotificationRepository.kt
package com.yoin.domain.repository

import com.yoin.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    /**
     * 通知設定を取得（リアクティブ）
     */
    fun getSettings(): Flow<NotificationSettings>

    /**
     * 通知設定を更新
     */
    suspend fun updateSettings(settings: NotificationSettings): Result<Unit>
}
```

```kotlin
// shared/src/commonMain/kotlin/com/yoin/data/repository/NotificationRepositoryImpl.kt
package com.yoin.data.repository

import com.yoin.data.dto.NotificationSettingsDto
import com.yoin.data.mapper.toDomain
import com.yoin.data.mapper.toDto
import com.yoin.domain.model.NotificationSettings
import com.yoin.domain.repository.NotificationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotificationRepositoryImpl(
    private val supabase: SupabaseClient,
    private val getCurrentUserId: () -> String
) : NotificationRepository {

    override fun getSettings(): Flow<NotificationSettings> {
        return supabase.from("notification_settings")
            .select {
                filter { eq("user_id", getCurrentUserId()) }
            }
            .decodeAsFlow<NotificationSettingsDto>()
            .map { it.toDomain() }
    }

    override suspend fun updateSettings(settings: NotificationSettings): Result<Unit> {
        return try {
            supabase.from("notification_settings")
                .update(settings.toDto()) {
                    filter { eq("user_id", getCurrentUserId()) }
                }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
```

---

## 🎯 開発フローのベストプラクティス

### 1. ボトムアップ開発（推奨）

```
Domain → Data → Presentation
```

**メリット**:
- ビジネスロジックが先に固まり、UIは後から変更しやすい
- テストが書きやすい
- ドメイン駆動設計（DDD）の原則に従う

**手順**:
1. Domainモデルとビジネスルールを定義
2. Repositoryインターフェースを定義
3. UseCaseを実装
4. Repositoryの実装（Data層）
5. UI（Presentation層）

---

### 2. トップダウン開発

```
Presentation → Domain → Data
```

**メリット**:
- UIのイメージが先に固まる
- プロトタイプを早く作れる

**デメリット**:
- ビジネスロジックが曖昧なまま進む可能性
- 後でリファクタリングが必要になることが多い

---

### 3. 反復開発（推奨）

```
1. まず最小限で動くものを作る（Domain + Data）
2. UIを追加
3. テストを追加
4. リファクタリング
5. 次の機能へ
```

**メリット**:
- 早く動くものができる
- フィードバックループが速い
- 継続的な改善

---

## ⚡ 効率的な開発のコツ

### AIエージェント（Claude Code）の活用

プロジェクトに用意されているカスタムコマンドを活用しましょう。

```bash
# 新機能を追加する際のテンプレート
/add-feature

# コードレビューを依頼
/review-code

# アーキテクチャを確認
/explain-architecture
```

### コンテキストの参照

AIエージェントは `.claude/context.md` を参照して、プロジェクトの全体像を理解しています。
新しい機能を追加する際は、このコンテキストを更新しておくと良いでしょう。

---

### テスト駆動開発（TDD）

```kotlin
// 1. テストを先に書く（Red）
@Test
fun `should upload photo successfully`() {
    // Given
    val imageBytes = byteArrayOf(1, 2, 3)
    coEvery { repository.uploadPhoto(any(), any()) } returns Result.Success(mockPhoto)

    // When
    val result = useCase(roomId, imageBytes)

    // Then
    assertTrue(result is Result.Success)
}

// 2. 実装（Green）
class UploadPhotoUseCase(
    private val repository: PhotoRepository
) {
    suspend operator fun invoke(roomId: String, imageBytes: ByteArray): Result<Photo> {
        return repository.uploadPhoto(roomId, imageBytes)
    }
}

// 3. リファクタリング（Refactor）
// より良い実装に改善
```

---

### コードレビューのポイント

自己レビューまたはAIレビューで以下を確認：

1. **アーキテクチャ**: Clean Architectureに従っているか
2. **命名**: わかりやすい名前か
3. **エラーハンドリング**: Result型でラップされているか
4. **テスト**: 主要なロジックにテストがあるか
5. **パフォーマンス**: 不要な処理がないか
6. **セキュリティ**: 個人情報の扱いは適切か

```bash
# AIにコードレビューを依頼
/review-code
```

---

## 📚 参考ドキュメント

- [アーキテクチャ設計](architecture.md) - レイヤー構成、MVIパターン
- [コーディング規約](coding-standards.md) - 命名規則、ベストプラクティス
- [開発ガイドライン](development-guide.md) - 環境構築、テスト
- [API連携ガイド](api-integration.md) - Supabase/Firebase連携
- [.claude/context.md](../.claude/context.md) - プロジェクト全体のコンテキスト

---

**最終更新**: 2024年12月
