package com.yoin.data.client.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import org.koin.dsl.module

/**
 * データクライアント層のDIモジュール
 *
 * Supabase、Firebase、RevenueCatなどのSDKクライアントを提供
 */
val dataClientModule = module {
    // Supabase Client
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Postgrest)
            install(Auth)
            install(Storage)
            install(Realtime)
        }
    }
}

// Supabase設定
// NOTE: これらの値は公開されても問題ない情報です（Anon Keyは公開用キー）
// Row Level Security (RLS)により、データへのアクセスは保護されます
private const val SUPABASE_URL = "https://wastxayoabzpehpajvni.supabase.co"
private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Indhc3R4YXlvYWJ6cGVocGFqdm5pIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjY3NDM5NzksImV4cCI6MjA4MjMxOTk3OX0.P_fTr8vzZwT74qUa5yYoaj7dYCYMLa4Mtx2NDs2lr44"
