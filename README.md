# RESTAdapter for Android RecyclerView
### Extremely easy setup
Gradle
```gradle
compile 'com.suhel.restadapter:rest-adapter:5.0'
```
Maven
```xml
<dependency>
    <groupId>com.suhel.restadapter</groupId>
    <artifactId>rest-adapter</artifactId>
    <version>3.0</version>
    <type>pom</type>
</dependency>
```
Ivy
```xml
<dependency org='com.suhel.restadapter' name='rest-adapter' rev='3.0'>
  <artifact name='rest-adapter' ext='pom' ></artifact>
</dependency>
```
### Usage
##### Create a JSON in `res/raw` folder
###### `my_json.json`
```json
{
  "baseURL": "https://jsonplaceholder.typicode.com",
  "projections": {
    "posts": {
      "requestPath": "/comments",
      "responsePath": "/",
      "viewId": "item_card",
      "viewMap": {
        "email": "tvTitle",
        "body": "tvBody"
      },
      "dataTypeMap": {
        "email": "text",
        "body": "text"
      }
    }
  }
}
```
##### Create the adapter
###### MainActivity.java
```java
RESTAdapter adapter = new RESTAdapter.Builder()
                            .withContext(this)
                            .fromJSON(R.raw.my_json)
                            .loadProjection("posts")
                            .build();
RecyclerView list = (RecyclerView) findViewById(R.id.list);
list.setLayoutManager(new LinearLayoutManager(this));
list.setAdapter(adapter);
adapter.fetch();
```
##### Additionally you can use the setOnClickListener() on the adapter for speed
```java
adapter.setOnClickListener(new RESTAdapter.OnClickListener() {
    
    @Override
    public void onClick(int position) {
        handleClick(position);
    }
});
```
### Customization
##### Different response path
For example, the JSON array resides in a sub node inside the JSON response sent from server
Update the JSON
###### my_json.json
```json
{
  "baseURL": "https://reqres.in/api",
  "projections": {
    "posts": {
      "requestPath": "/users?page=2",
      "responsePath": "/data",
      "viewId": "item_card",
      "viewMap": {
        "email": "tvTitle",
        "body": "tvBody"
      },
      "dataTypeMap": {
        "email": "text",
        "body": "text"
      }
    }
  }
}
```
##### Parameters in request path
For example, the `page` parameter needs a dynamic value for each request in the path `/users?page=2`
Update the JSON
###### my_json.json
```json
{
  "baseURL": "https://reqres.in/api",
  "projections": {
    "posts": {
      "requestPath": "/users?page={pageNo}",
      "responsePath": "/data",
      "viewId": "item_card",
      "viewMap": {
        "email": "tvTitle",
        "body": "tvBody"
      },
      "dataTypeMap": {
        "email": "text",
        "body": "text"
      }
    }
  }
}
```
Call `setArgsMap()` on the adapter with a `Map` of arguments and their values.
##### MainActivity.java
```java
RESTAdapter adapter = new RESTAdapter.Builder()
                            .withContext(this)
                            .fromJSON(R.raw.my_json)
                            .loadProjection("posts")
                            .build();
RecyclerView list = (RecyclerView) findViewById(R.id.list);
list.setLayoutManager(new LinearLayoutManager(this));
list.setAdapter(adapter);
Map<String, String> argsMap = new HashMap<>();
argsMap.put("pageNo", "2");
adapter.setArgsMap(argsMap);
adapter.fetch();
```
##### Incoming JSON contains fields in different paths
For example, the `email` parameter is in a different path and the `body` is in another
Update the JSON
###### my_json.json
```json
{
  "baseURL": "https://reqres.in/api",
  "projections": {
    "posts": {
      "requestPath": "/users?page={pageNo}",
      "responsePath": "/data",
      "viewId": "item_card",
      "viewMap": {
        "basic/email": "tvTitle",
        "details/body": "tvBody"
      },
      "dataTypeMap": {
        "basic/email": "text",
        "details/body": "text"
      }
    }
  }
}
```
