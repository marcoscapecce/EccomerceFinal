# E-commerce TechLab

Aplicación de ejemplo de gestión de productos y pedidos, desarrollada con Java, Spring Boot y una interfaz web estática.

## Requisitos

- Java 21
- Maven 3.6 o posterior

## Ejecutar

Desde la carpeta del proyecto:

```bash
mvn spring-boot:run
```

La aplicación inicia una base de datos H2 en memoria y carga los productos definidos en `src/main/resources/data.sql`. Los datos se reinician al volver a iniciar la aplicación.

Abre [http://localhost:8080/](http://localhost:8080/) en el navegador. También puedes acceder a [http://localhost:8080/index.html](http://localhost:8080/index.html).

Al iniciar, la aplicación también muestra un menú de consola. El servidor web puede atender solicitudes mientras el menú espera una opción; selecciona `3` para salir del menú. Para detener la aplicación, usa `Ctrl+C`.

## API

Las rutas principales son:

| Método | Ruta | Descripción |
| --- | --- | --- |
| `GET` | `/api/productos` | Lista los productos |
| `POST` | `/api/productos` | Crea un producto |
| `POST` | `/api/pedidos` | Procesa un pedido |
| `GET` | `/api/usuarios/{id}/pedidos` | Consulta los pedidos de un usuario |

## Desarrollo

Compilar:

```bash
mvn -DskipTests compile
```

Ejecutar las pruebas:

```bash
mvn test
```

Para importarlo en Eclipse, selecciona **File → Import → Existing Maven Projects** y elige la carpeta del proyecto.
