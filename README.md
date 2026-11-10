# TiendaTech – Tienda virtual para Android (Frontend)

Actividad 3 · Desarrollo de aplicaciones móviles · Fundación Universitaria Compensar
Estudiante: César Sánchez Marín · Docente: Flavio Alexander Navarro Carmona

Frontend nativo en **Kotlin + XML (Material 3, ViewBinding)** de la tienda de accesorios tecnológicos
planeada en la Actividad 2. Los datos son de ejemplo (en memoria); el backend, la pasarela de pagos real
y las notificaciones quedan para la siguiente fase.

## Cómo abrirlo
1. Android Studio Koala (2024.1) o superior → **File > Open** → carpeta `TiendaTech`.
2. Esperar la sincronización de Gradle (JDK 17) y ejecutar en un emulador/celular Android 7.0+ (API 24).
3. Para probar la huella en el emulador: *Extended controls > Fingerprint* (y registrar una huella en Ajustes).

## Cuentas de prueba
| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@tienda.com | Admin123 |
| Vendedor | vendedor@tienda.com | Vend123 |
| Comprador | comprador@tienda.com | Comp123 |

La huella funciona después de haber iniciado sesión una vez con contraseña.

## Pantallas por rol
| Rol | Pantallas | Navegación |
|---|---|---|
| Transversal | Splash, Login (+huella), Registro, Recuperar contraseña, Perfil | Bottom Navigation + menú de Toolbar (cerrar sesión) |
| Administrador | Dashboard, Usuarios (listar/crear/editar/eliminar), Productos, Reporte de ventas | Panel · Usuarios · Productos · Ventas · Perfil |
| Vendedor | Productos (CRUD), Pedidos recibidos (cambiar estado), Perfil | Productos · Pedidos · Perfil |
| Comprador | Inicio, Catálogo, Detalle, Carrito, Pasarela de pagos, Mis compras, Perfil | Inicio · Catálogo · Carrito · Perfil |

## Estructura
```
app/src/main/
├── java/.../tiendatech/
│   ├── data/    Models.kt, Repo.kt (datos de ejemplo)
│   ├── ui/      Splash/Auth/MainActivity, Helpers.kt (VBFragment, SimpleAdapter)
│   │   ├── auth/ admin/ seller/ buyer/ shared/   (Fragments por rol)
└── res/
    ├── layout/  activity_*, fragment_*, item_*, dialog_*
    ├── menu/    menu_admin, menu_seller, menu_buyer, menu_toolbar
    ├── drawable/ vectores (ic_*) y fondos (bg_*)
    └── values/  colors, themes, styles, strings
```

## Diseño propio (no por defecto)
Paleta: azul marino `#1B2A49`, turquesa `#00A896`, coral `#FF6B4A`, fondo `#F4F7FA`.
Estilos personalizados en `styles.xml` (botones, campos, tarjetas, badges) y fondos con degradado en `drawable/`.
