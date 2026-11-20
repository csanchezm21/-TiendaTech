
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // Conecta la aplicación con la configuración de Firebase.
    id("com.google.gms.google-services")
}

android {
    namespace = "co.edu.compensar.tiendatech"
    compileSdk = 34

    defaultConfig {
        applicationId = "co.edu.compensar.tiendatech"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(
                org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Dependencias que ya tenía TiendaTech.
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.fragment:fragment-ktx:1.8.2")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.biometric:biometric:1.1.0")

    // Administra las versiones compatibles de Firebase.
    implementation(platform("com.google.firebase:firebase-bom:35.2.0"))

    // Autenticación de usuarios.
    implementation("com.google.firebase:firebase-auth")

    // Base de datos de usuarios y productos.
    implementation("com.google.firebase:firebase-firestore")

    // Almacenamiento de fotografías.
    implementation("com.google.firebase:firebase-storage")


    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
}
