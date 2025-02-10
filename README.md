## Configuración de la base de datos

### Docker   
  - Crear la imagen:  
  ~~~
    docker build -t my-mariadb .  
  ~~~
  - Crear el contenedor:  
  ~~~
    docker run -d --name mariadb_container -p 3307:3306 my-mariadb 
  ~~~
El contenedor está corriendo en el puerto 3307 para que no entre en conflicto con MariaDb en local. v
  
Para entrar en el contenedor y realizar consultas directamente a la base de datos:  
  ~~~  
    docker exec -it mariadb_container my-mariadb -u mainuser -p
  ~~~
***

### Local
   Si ya está instalado Mariadb en el equipo y se quiere ejecutar en local  
  - Cambiar el puerto en el fichero **application.properties** 
~~~  
spring.datasource.url=jdbc:mariadb://localhost:3306/esports  
~~~
Poner de nuevo el puerto 3306 o el que corresponda a Mariadb en local 

Para realizar consultas directamente a la base de datos, se puede conectar directamente si se tiene instalado la consola de Mariadb o bien desde la consola de comandos con:  
~~~  
mysql -u mainuser -p -h localhost -P 3306
~~~  
  
## Diagrama Entidad-Relación  
  
![APIdiagramaE-R.jpg](..%2F..%2FOneDrive%2FEscritorio%2FAPIdiagramaE-R.jpg)  
  
## Arrancar el proyecto  

~~~  
  mvn spring-boot:run
~~~    

La primera vez se instalaran todas las dependencias especificadas en el pom.xml  
Si alguna dependencia no se ha instalado bien o algo no funciona correctamente con esta comando se fuerza a recompilar:  

~~~  
  mvn clean install
~~~ 