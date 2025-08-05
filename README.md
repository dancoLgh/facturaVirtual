# Factura Virtual

Aplicación Android para generar y enviar facturas electrónicas ante la SET de Paraguay.

## Requisitos

- [Android Studio](https://developer.android.com/studio) o `gradlew` en la línea de comandos
- JDK 8 o superior
- Android SDK 28 (compileSdkVersion 28)

## Compilación

Clonar el repositorio y, desde la raíz del proyecto, ejecutar:

```bash
./gradlew assembleDebug
```

El APK de debug quedará disponible en `app/build/outputs/apk/`. También se incluye un APK precompilado en `app/release/app-release.apk` para pruebas rápidas.

## Ejecución

1. Conectar un dispositivo Android o iniciar un emulador.
2. Instalar el APK de `app/build/outputs/apk/debug/` con Android Studio o usando
   `adb install`.
3. Abrir la aplicación **Factura Virtual** en el dispositivo.

## Uso básico

1. En el primer inicio se solicitará completar el formulario de **Personalización** con los datos del contribuyente y las credenciales de acceso a la SET.
2. Luego se mostrará la **Pantalla Principal** con la información registrada. Desde aquí se puede editar la configuración o generar nuevas facturas.
3. En la opción **Facturas** se ingresan los datos del cliente, descripción del producto o servicio, cantidad, precio y tasa de IVA.
4. Al confirmar el formulario se genera la factura, se crea un PDF con el código QR correspondiente y se guarda en el almacenamiento del dispositivo para compartir o imprimir.

## Licencia

Este proyecto se distribuye bajo los términos especificados en el repositorio original.
