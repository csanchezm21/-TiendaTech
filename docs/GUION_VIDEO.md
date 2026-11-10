# Guion del video (máx. 5 min) – explicación técnica

| Tiempo | Qué mostrar | Qué decir |
|---|---|---|
| 0:00–0:20 | Estructura del proyecto en Android Studio | Presentación y arquitectura: paquetes `data`, `ui` y un paquete de Fragments por rol. |
| 0:20–1:10 | `values/colors.xml`, `themes.xml`, `styles.xml` | Tema Material 3 con paleta propia; estilos reutilizables (`Widget.Tienda.Button`, `Tienda.Card`, `Tienda.Badge`). |
| 1:10–1:50 | `drawable/` (vectores `ic_*`, `bg_gradient_primary`, `bg_banner`) | Vector drawables, shapes con degradado y esquinas redondeadas; selector de color `nav_item_color`. |
| 1:50–2:40 | `activity_main.xml`, `menu_*.xml`, `MainActivity.kt` | Un solo Activity con Toolbar + BottomNavigation; el menú se infla según el rol; `Titled` pone el título. |
| 2:40–3:20 | `fragment_login.xml`, `LoginFragment.kt` | TextInputLayout con validación; BiometricPrompt para la huella; Splash → Auth → Main. |
| 3:20–4:10 | `item_product_card.xml`, `Helpers.kt`, `CatalogFragment.kt` | RecyclerView en cuadrícula con `SimpleAdapter` genérico, filtros con ChipGroup y búsqueda. |
| 4:10–4:40 | `dialog_user.xml`, `UsersFragment.kt` | CRUD con MaterialAlertDialog y validaciones. |
| 4:40–5:00 | Demo rápida de los 3 roles en el emulador | Cierre y trabajo futuro (backend, pagos reales, cámara y ubicación). |
