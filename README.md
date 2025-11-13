# 📦 Inventory Widget App

Es una Aplicación móvil desarrollada en **Android (Kotlin)** como parte del **Miniproyecto 1** de la asignatura *Desarrollo de Aplicaciones para Dispositivos Móviles* en la **Universidad del Valle**.

El objetivo principal del proyecto es **crear una aplicación de inventario con un widget interactivo**, que permita al usuario visualizar, agregar, editar y eliminar productos, así como autenticarse mediante huella digital.

---

## 🚀 Descripción General

**Inventory Widget App** implementa una arquitectura **MVVM con Repository**, base de datos local mediante **Room (SQLite)**, y navegación entre pantallas usando **Fragments** y el **Navigation Component**.

Además, la app incluye:
- Un **widget personalizado** para mostrar el saldo total del inventario.
- **Autenticación biométrica (huella dactilar)**.
- Persistencia de sesión con **SharedPreferences**.
- Diseño con enfoque en **UI/UX** y paleta de colores oscuros.

---

## 🌿 Estructura del Repositorio

Cada rama (`branch`) del repositorio corresponde a una **Historia de Usuario (HU)** del sprint:

| Rama | Historia de Usuario | Descripción breve |
|------|----------------------|-------------------|
| `HU1-widget` | HU 1.0 | Creación del widget principal con funcionalidad para mostrar/ocultar saldo e ir a gestión de inventario. |
| `HU2-login` | HU 2.0 | Implementación del login con autenticación por huella digital. |
| `HU3-home` | HU 3.0 | Ventana principal del inventario: listado de productos, sesión activa y botón flotante. |
| `HU4-agregar-producto` | HU 4.0 | Formulario para agregar productos nuevos usando Room. |
| `HU5-detalle-producto` | HU 5.0 | Pantalla de detalle del producto con opción de eliminar y enlace a edición. |
| `HU6-editar-producto` | HU 6.0 | Edición de productos existentes con actualización en base de datos. |

> 💡 La rama principal `main` integra todas las funcionalidades completadas de las historias anteriores.

---

## ⚙️ Tecnologías y Herramientas

- **Lenguaje:** Kotlin  
- **Arquitectura:** MVVM + Repository  
- **Base de Datos:** Room (SQLite)  
- **Navegación:** Fragments + Navigation Component  
- **Persistencia:** SharedPreferences  
- **Interfaz:** Material Design Components  
- **Gestión de Código:** GitHub  
- **Gestión de Tareas:** Jira  

---

## 👨‍💻 Desarrollado por
Estudiantes de la **Escuela de Ingeniería de Sistemas y Computación**  

- Juan Felipe Palechor
- Esteban Camilo Martinez
- Jhon Frank Vasquez
  
**Universidad del Valle — 2025**  
Docente: *Ing. Walter Medina*

---

> 📘 Este repositorio hace parte de un proyecto académico, desarrollado con fines de aprendizaje.
