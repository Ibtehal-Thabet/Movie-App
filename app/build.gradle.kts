plugins {
    id("com.android.application")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

//val apiKey = project.hasProperty("SUPABASE_API_KEY") ? project.SUPABASE_API_KEY : ""
val supabaseApiKey = System.getenv("SUPABASE_API_KEY") ?: ""
val moviesApiKey = System.getenv("MOVIES_API_KEY") ?: ""
val projectKey = System.getenv("PROJECT_KEY") ?: ""
android {
    namespace = "com.example.movieapp"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.movieapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "SUPABASE_API_KEY",
            "\"${supabaseApiKey}\""
        )
        buildConfigField("String", "MOVIES_API_KEY", "\"${moviesApiKey}\"")
        buildConfigField("String", "PROJECT_KEY", "\"${projectKey}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
        android.buildFeatures.buildConfig = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation("com.github.Dimezis:BlurView:version-2.0.5")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("androidx.security:security-crypto:1.0.0")
//    implementation("com.prolificinteractive:material-calendarview:2.0.1")
//    implementation("io.supabase:supabase-android:0.3.0")
//    implementation("com.github.supabase:supabase-java:Tag")
//    implementation("io.supabase:supabase-kt:0.9.0")
//    implementation("io.github.jan-tennert.supabase:postgrest-kt:0.7.6")
//    implementation("io.ktor:ktor-client-apache5:2.3.3")
//    implementation("org.jetbrains.kotlinx:kot.serialization")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}