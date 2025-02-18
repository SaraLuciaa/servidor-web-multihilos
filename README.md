# Servidor Web con ThreadPool

Este es un servidor web multihilo en Java que utiliza un `ThreadPool` para manejar múltiples solicitudes de clientes de manera eficiente.

## Cómo compilar y ejecutar el servidor

1. **Compilar el código fuente:**
   ```sh
   javac ServidorWeb.java
   ```

2. **Ejecutar el servidor:**
   ```sh
   java ServidorWeb
   ```
   Esto iniciará el servidor en el puerto `6789` y estará listo para recibir conexiones.

## Cómo probar el servidor

### 1. Acceder desde un navegador
Abre un navegador web e ingresa la siguiente URL:
```
http://localhost:6789/index.html
```
Reemplaza `index.html` con el nombre del archivo que deseas solicitar.

### 3. Comprobar en la consola del servidor
Cada vez que un archivo es solicitado y encontrado, el servidor imprimirá un mensaje en la consola:
```
Archivo encontrado: ./index.html
```
Si el archivo no existe, se enviará una respuesta `404 Not Found`.

## Agregar archivos al servidor
Coloca los archivos HTML, imágenes o cualquier otro recurso en el mismo directorio donde se ejecuta el servidor para que puedan ser servidos correctamente.

## Detener el servidor
Para detener el servidor, usa `Ctrl + C` en la terminal donde se está ejecutando.

## Notas
- Si el servidor devuelve `404 Not Found` para archivos que existen, asegúrate de ejecutarlo en el directorio correcto donde están los archivos.
- El servidor maneja solicitudes básicas `GET` y sirve archivos estáticos.
