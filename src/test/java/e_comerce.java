import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import com.jayway.jsonpath.JsonPath;
import java.util.Base64;

import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class e_comerce {

    //variables
    static private String base_url = "webapi.segundamano.mx";
    static private String email = "dulcemariagb@hotmail.com";
    static private String password = "1234567890";
    static private String access_token;
    static private String account_id;
    static private String uuid;
    static private String address_id;
    static private String ad_id;
    static private String favorito_id;
    static private String alert_id;

    //Funcion para crear un token
    private String getToken(){
        //https://webapi.segundamano.mx/nga/api/v1.1/private/accounts?lang=es"

        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts", base_url);
        System.out.println("end point de la funcion getToken : " + baseURI);

        Response resp = given()
                .log().all()
                .queryParam("lang", "es")
                .auth().preemptive().basic(email, password)
                .post();

        String body_response = resp.getBody().asString();
        System.out.println("Body response: " + body_response);


        access_token = JsonPath.read(body_response, "$.access_token");
        System.out.println("token: " + access_token);

        uuid = JsonPath.read(body_response, "$.account.uuid");
        System.out.println("uuid: " + uuid);

        String datos = uuid + ":" + access_token;

        String encodedAuth = Base64.getEncoder().encodeToString(datos.getBytes());

        return encodedAuth;
    }

    @Test
    public void t01_obtener_categorias(){
        //https://webapi.segundamano.mx/nga/api/v1/public/categories/insert?lang=es

        RestAssured.baseURI = String.format("https://%s/nga/api/v1/public/categories/insert",base_url);
        System.out.println("end point de obtener categorias: " + baseURI);

        //hacer el request y guardarlo en response
        Response response = given()
                .log()
                .all()
                .queryParam("lang","es")
                .get();

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();
        //String body_p = response.prettyPrint();
        System.out.println("Body response: " + body_response);
        System.out.println("Headers response: " + headers_response);
        //System.out.println("Body response: " + body_p);

        assertEquals(200,response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("categories"));
    }

    @Test
    public void t02_obtener_token_Basic_Header(){
        //El encode del correo y del password no se programa.
        //Como son datos que no cambian se trabaja con un encode fijo
        //Se obtiene el access_code y el account_id
        //Se genera el token a traves del header pasandole el
        //encode de la concatenacion del email y el password

        //https://webapi.segundamano.mx/nga/api/v1.1/private/accounts

        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",base_url);
        String token_basic = "ZHVsY2VtYXJpYWdiQGhvdG1haWwuY29tOjEyMzQ1Njc4OTA=";

        Response response = given()
                .log().all()
                .queryParam("lang","es")
                .header("Authorization","Basic " + token_basic)
                .post();

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();
        //String body_p = response.prettyPrint();
        System.out.println("Body response: " + body_response);
        System.out.println("Headers response: " + headers_response);
        //System.out.println("Body response: " + body_p);

        access_token = JsonPath.read(body_response, "$.access_token");
        System.out.println("token con un encode fijo: " + access_token);

        account_id = JsonPath.read(body_response, "$.account.account_id");
        System.out.println("account_id: " + account_id);

        uuid = JsonPath.read(body_response, "$.account.uuid");
        System.out.println("uuid: " + JsonPath.read(body_response, "$.account.uuid"));

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("access_token"));
        assertTrue(body_response.contains("account_id"));
        assertTrue(body_response.contains("uuid"));
    }

    @Test
    public void t03_obtener_token_Basic_Auth_email_pass() {
        //Se obtiene el access_code y el account_id
        //Se genera el token a traves de la funcionalidad de
        //Authorization pasandole el correo y password planos
        //y automaticamente se encodean los datos y se concatenan

        //https://webapi.segundamano.mx/nga/api/v1.1/private/accounts

        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts", base_url);

        Response response = given()
                .log().all()
                .queryParam("lang", "es")
                .auth().preemptive().basic(email, password)
                .post();

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();
        //String body_p = response.prettyPrint();
        System.out.println("Body response: " + body_response);
        System.out.println("Headers response: " + headers_response);
        //System.out.println("Body response: " + body_p);


        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("access_token"));
        assertTrue(body_response.contains("account_id"));
        assertTrue(body_response.contains("uuid"));

        access_token = JsonPath.read(body_response, "$.access_token");
        System.out.println("token encodeado por la Authorization: " + JsonPath.read(body_response, "$.access_token"));

        account_id = JsonPath.read(body_response, "$.account.account_id");
        System.out.println("account_id: " + JsonPath.read(body_response, "$.account.account_id"));

        uuid = JsonPath.read(body_response, "$.account.uuid");
        System.out.println("uuid: " + JsonPath.read(body_response, "$.account.uuid"));
    }

    @Test
    public void t04_editar_datos_usuario(){
        //https://webapi.segundamano.mx/nga/api/v1/private/accounts/11504746

        RestAssured.baseURI = String.format("https://%s/nga/api/v1/%s",base_url,account_id);
        System.out.println("end point de editar datos de usuario: " + baseURI);

        String body = "{\"account\":{\"name\":\"Carlos Chavira\",\"phone\":\"2288123456\",\"professional\":false}}";

        Response response = given()
                .log().all()
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type","application/json")
                .header("Origin","https://www.segundamano.mx")
                .body(body)
                .patch();

        String body_response = response.getBody().asString();
        String headers_response = response.getHeaders().toString();
        //String body_p = response.prettyPrint();
        System.out.println("Body response: " + body_response);
        System.out.println("Headers response: " + headers_response);
        //System.out.println("Body response: " + body_p);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("account"));
        assertNotNull(body_response);
    }

    @Test
    public void t05_crear_direccion(){
        //https://webapi.segundamano.mx/addresses/v1/create

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/addresses/v1/create",base_url);
        System.out.println("end point de crear direccion : " + baseURI);

        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .header("Content-type","application/x-www-form-urlencoded")
                .formParam("contact","Rosita Quintana Guevara")
                .formParam("phone","5566990099")
                .formParam("rfc","")
                .formParam("zipCode","91020")
                .formParam("exteriorInfo","Lopez Mateos 76867")
                .formParam("region","5")
                .formParam("municipality","51")
                .formParam("alias","La casa del abuelo")
                .post();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        assertEquals(201, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("addressID"));

        address_id = JsonPath.read(body_response, "$.addressID");
        System.out.println("address id: " + address_id);
    }

    @Test
    public void t06_leer_direcciones(){
        //https://webapi.segundamano.mx/addresses/v1/get

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/addresses/v1/get/",base_url);
        System.out.println("end point de leer direccion : " + baseURI);

        Response response =  given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .get();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("addresses"));
    }

    @Test
    public void t07_modificar_direccion(){
        //https://webapi.segundamano.mx/addresses/v1/modify/8bdf59f6-5f83-11ec-94cb-abc0eb6fc430

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/addresses/v1/modify/%s",base_url,address_id);
        System.out.println("end point de modificar direccion : " + baseURI);

        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .header("Content-type","application/x-www-form-urlencoded")
                .formParam("contact","Enrique Rosas")
                .formParam("phone","1111111111")
                .formParam("rfc","GOMA670203")
                .formParam("zipCode","45088")
                .formParam("exteriorInfo","Lopez Mateos 76867")
                .formParam("region","5")
                .formParam("municipality","51")
                .formParam("alias","Segundo frente del abuelo")
                .put();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains(address_id));

        assertTrue(body_response.contains("{\"message\":\""+ address_id +" modified correctly\"}"));
    }

    @Test
    public void t08_borrar_direccion(){
        //https://webapi.segundamano.mx/addresses/v1/delete/6309e966-5f91-11ec-9d8d-c738d5f9c865

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/addresses/v1/delete/%s",base_url,address_id);
        System.out.println("end point de eliminar direccion : " + baseURI);

        Response response =  given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .delete();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains(address_id));

        assertTrue(body_response.contains("{\"message\":\""+ address_id +" deleted correctly\"}"));
    }

    @Test
    public void t09_crear_anuncio(){
      //https://webapi.segundamano.mx/v2/accounts/d96de33c-df53-4fb3-bb37-6f55b75c0da4/up

        //funcion generar un token
        //leer token
        //hacer request crear anuncio

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/v2/accounts/%s/up",base_url,uuid);
        System.out.println("end point de crear anuncio : " + baseURI);

        String body = "{\"images\":\"7815466751.jpg\",\"category\":\"8144\",\"subject\":\"Chiles cuaresmeños\",\"body\":\"Son capeados y rellenos de queso.\",\"price\":\"18\",\"region\":\"32\",\"municipality\":\"2163\",\"area\":\"24743\",\"phone_hidden\":\"true\",\"show_phone\":\"false\",\"contact_phone\":\"1234567890\"}";

        Response response = given()
                .log().all()
                .header("Authorization","Basic "+ newToken)
                .header("Content-Type","application/json")
                .header("x-source","PHOENIX_DESKTOP")
                .header("Accept","application/json, text/plain, */*")
                .body(body)
                .post();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("ad_id"));

        ad_id = JsonPath.read(body_response, "$.data.ad.ad_id");
        System.out.println("ad id: " + ad_id);
    }

    @Test
    public void t10_consultar_anuncios(){
        //https://webapi.segundamano.mx/nga/api/v1/private/accounts/11504746/klfst?status=active&lim=20&o=0&query=&lang=es

        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s/klfst",base_url,account_id);
        System.out.println("end point de consultar anuncios : " + baseURI);

        Response response = given()
                .log().all()
                .param("status", "active")
                .param("lim", "20")
                .param("o","0")
                .param("query", "")
                .param("lang","es")
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .header("Origin", "https://www.segundamano.mx")
                .header("Accept","application/json, text/plain, */*")
                .get();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        int no_anuncios = JsonPath.read(body_response, "$.counter_map.active");
        System.out.println("Anuncios Activos: " + JsonPath.read(body_response, "$.counter_map.active"));

        String cadena_ad_id = JsonPath.read(body_response, "$.private_ads[0].ad.ad_id");
        System.out.println("cadena ad id: " + cadena_ad_id);
        ad_id = cadena_ad_id.substring(31);
        System.out.println("ad id: " + ad_id);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("active"));
        assertNotEquals(no_anuncios, 0);
    }

    @Test
    public void t11_modificar_anuncio(){
      //https://webapi.segundamano.mx/accounts/d96de33c-df53-4fb3-bb37-6f55b75c0da4/up/72956907

        //funcion generar un token
        //leer token
        //hacer request modificar anuncio

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/accounts/%s/up/%s",base_url,uuid,ad_id);
        System.out.println("end point de modificar anuncio : " + baseURI);

        String body = "{\"images\":\"7815466751.jpg\",\"category\":\"8144\",\"subject\":\"Chiles habaneros\",\"body\":\"Rellenos de queso de cabra.\",\"price\":\"20\",\"region\":\"32\",\"municipality\":\"2163\",\"area\":\"24743\",\"phone_hidden\":\"true\",\"show_phone\":\"false\",\"contact_phone\":\"3344556677\"}";

        Response response = given()
                .log().all()
                .header("Authorization","Basic "+ newToken)
                .header("Content-Type","application/json")
                .header("x-source","PHOENIX_DESKTOP")
                .header("Accept","application/json, text/plain, */*")
                .body(body)
                .put();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains(ad_id));
    }

    @Test
    public void t12_eliminar_anuncio(){
        //https://webapi.segundamano.mx/nga/api/v1/private/accounts/11504746/klfst/72956875
        //funcion generar un token
        //leer token
        //hacer request eliminar anuncio

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s/klfst/%s",base_url,account_id,ad_id);
        System.out.println("end point de eliminar anuncio : " + baseURI);

        String body = "{\"delete_reason\": {\"code\": \"5\"}}";

        Response response = given()
                .log().all()
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type","application/json")
                .header("Origin","https://www.segundamano.mx")
                .header("Accept","application/json, text/plain, */*")
                .body(body)
                .delete();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("{\"action\":{\"action_type\":\"delete\"}}"));
    }


    @Test
    public void t13_Editar_datos_usuario_inexistente(){
        //https://webapi.segundamano.mx/nga/api/v1/private/accounts/11504746?lang=es

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        String cadena_account_id = account_id;
        cadena_account_id = cadena_account_id.replace("11504746", "99504746");
        System.out.println("account_id original: " + account_id);
        System.out.println("account_id cambiado: " + cadena_account_id);

        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s",base_url,cadena_account_id);
        System.out.println("end point de editar_datos_usuario : " + baseURI);

        String body = "{\"account\":{\"name\":\"Dulcecito\",\"phone\":\"8884567890\",\"professional\":false}}";

        Response response = given()
                .log().all()
                .param("lang", "es")
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type","application/json")
                .header("Origin","https://www.segundamano.mx")
                .header("Accept","application/json, text/plain, */*")
                .body(body)
                .patch();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        assertEquals(403, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("{\"error\":{\"code\":\"FORBIDDEN\"}}"));
    }


    @Test
    public void t14_Consultar_balance_monedas_virtuales(){
        //https://webapi.segundamano.mx/nga/api/v1/api/users/d96de33c-df53-4fb3-bb37-6f55b75c0da4/counter?lang=es

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/nga/api/v1/api/users/uuid/counter",base_url,uuid);
        System.out.println("end point de consultar balance monedas virtuales : " + baseURI);

        Response response = given()
                .log().all()
                .param("lang", "es")
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type","application/json")
                .header("Origin","https://www.segundamano.mx")
                .header("Accept","application/json, text/plain, */*")
                .get();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        assertEquals(403, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("{\"error\":{\"code\":\"FORBIDDEN\"}}"));
    }

    @Test
    public void t15_Cambiar_password(){
        //https://webapi.segundamano.mx/nga/api/v1/private/accounts/11504746?lang=es

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s",base_url,account_id);
        System.out.println("end point de cambiar password : " + baseURI);

        Response response = given()
                .log().all()
                .param("lang", "es")
                .header("Authorization","tag:scmcoord.com,2013:api " + access_token)
                .header("Content-Type","application/json")
                .header("Origin","https://www.segundamano.mx")
                .header("Accept","application/json, text/plain, */*")
                .body("{\"account\":{\"password\":\"0987654321\"}}")
                .patch();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));
        password = "0987654321";

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains(account_id));
        assertTrue(body_response.contains("dulcemariagb@hotmail.com"));
    }

    @Test
    public void t16_Agregar_favorito(){
        //https://webapi.segundamano.mx/favorites/v1/private/accounts/d96de33c-df53-4fb3-bb37-6f55b75c0da4

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/favorites/v1/private/accounts/%s",base_url,uuid);
        System.out.println("end point de Agregar favorito : " + baseURI);

        String body = "{\"list_ids\":[937312032]}";

        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .header("Origin","https://www.segundamano.mx")
                .header("Accept","application/json, text/plain, */*")
                .header("Content-Type", "application/json;charset=UTF-8")
                .body(body)
                .post();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("added"));
    }

    @Test
    public void t17_Consultar_favoritos(){
        //https://webapi.segundamano.mx/favorites/v1/private/accounts/d96de33c-df53-4fb3-bb37-6f55b75c0da4

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/favorites/v1/private/accounts/%s",base_url,uuid);
        System.out.println("end point de consultar favoritos : " + baseURI);

        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .header("Origin","https://www.segundamano.mx")
                .header("Accept","application/json, text/plain, */*")
                .get();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        String lista = body_response.substring(body_response.indexOf('9'));
        favorito_id = lista.substring(0, 9);
        System.out.println("favorito_id: " + favorito_id);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("Favoritos"));
    }

    @Test
    public void t18_Borrar_favorito(){
        //https://webapi.segundamano.mx/favorites/v1/private/accounts/d96de33c-df53-4fb3-bb37-6f55b75c0da4

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/favorites/v1/private/accounts/%s",base_url,uuid);
        System.out.println("end point de Borrar favorito : " + baseURI);

        String body = "{\"list_ids\":["+favorito_id+"]}";
        System.out.println("eliminar favorito_id: "+ favorito_id);

        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .header("Origin","https://www.segundamano.mx")
                .header("Accept","application/json, text/plain, */*")
                .header("Content-Type", "application/json")
                .body(body)
                .delete();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("deleted"));
    }

    @Test
    public void t19_Consultar_alertas(){
        //https://webapi.segundamano.mx/alerts/v1/private/account/d96de33c-df53-4fb3-bb37-6f55b75c0da4/alert

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/alerts/v1/private/account/%s/alert",base_url,uuid);
        System.out.println("end point de consultar alertas : " + baseURI);

        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .header("Origin","https://www.segundamano.mx")
                .header("Accept","application/json, text/plain, */*")
                .header("Content-Type", "application/json")
                .get();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        alert_id = JsonPath.read(body_response, "$.data.alerts[0].id");
        System.out.println("alert id: " + alert_id);

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("alerts"));
    }

    @Test
    public void t20_Eliminar_alerta(){
        //https://webapi.segundamano.mx/alerts/v1/private/account/d96de33c-df53-4fb3-bb37-6f55b75c0da4/alert/1c564696-6b77-41dc-938d-8e7b3ee20a09

        String newToken = getToken();
        System.out.println(("Test que regresa la funcion: " + newToken));

        RestAssured.baseURI = String.format("https://%s/alerts/v1/private/account/%s/alert/%s",base_url,uuid, alert_id);
        System.out.println("end point de eliminar alerta : " + baseURI);

        Response response = given()
                .log().all()
                .auth().preemptive().basic(uuid,access_token)
                .header("Origin","https://www.segundamano.mx")
                .header("Accept","application/json, text/plain, */*")
                .delete();

        String body_response = response.getBody().asString();
        System.out.println(("Body response: " + body_response));

        assertEquals(200, response.getStatusCode());
        assertNotNull(body_response);
        assertTrue(body_response.contains("ok"));
    }

}