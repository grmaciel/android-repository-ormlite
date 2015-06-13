# android-repository-ormlite
This is a project that aims to help people with their persistence layer using the repository pattern. The layer is built on top of Android OrmLite (https://github.com/j256/ormlite-android) and still is a working in progress.

## Setup

First you have to setup this project running in your local AndroidStudio or a Local Nexus Sonatype. You should use as a as dependency for your main project, not as a module since the object (at least in my view) is to use it across multiple projects.

**Referencing a project from another project with gradle**

At your ```settings.gradle``` declare de persistence layer:

```
include 'persistence'
project (':persistence').projectDir = new File('../android-repository-ormlite')
```

Declare the dependency at your app ```build.gradle```:

```
compile project (':persistence')
```

and you are all set to use the library.


## Usage

**First step, create your own DbHelper**

```
public class MyCustomDBHelper extends AbstractDBHelper {
    public MyCustomDBHelper(Context context, String databaseName) {
        super(context, databaseName);
    }

    @Override
    protected Class<?>[] getTableClassList() {
        return new Class<?>[]{MyModel.class};
    }
}
```

**Second step, create a model**

You can create a model for your own using the OrmLite documentation or you can extend the ```BaseModel``` class that offers a model with the id field declared, nothing fancy.
```
@DatabaseTable
public class MyModel extends BaseModel{
    @DatabaseField(canBeNull = true)
    private String fieldOne;
    @DatabaseField(canBeNull = true)
    private Integer fieldTwo;
}
```

For more info on setting up the the fields and rellations, check OrmLite Documentation.

**Third step, create an abstract repository to instanciate the helper**

This is the repository that the other repositories will extends, thats because you need to inject a reference to the DbHelper your application will be using.
```
public abstract class MyBaseRepository<T, Id> extends BaseOrmLiteRepository<T, Id>{
    public MyBaseRepository(Context context) {
        super(context, MyCustomDBHelper.class);
    }
}
```
You can also specify the kind of Id you will be using across the models, like this:
```
public abstract class MyBaseRepository<T> extends BaseOrmLiteRepository<T, Long>{
    public MyBaseRepository(Context context) {
        super(context, MyCustomDBHelper.class);
    }
}
```
**Fourth step, now it's time to create a real repository:**
```
public class MyModelRepository extends MyBaseRepository<MyModel> {
    public MyModelRepository(Context context) {
        super(context);
    }
}
```

This will provide your repository with a bunch of already implemented methods to persist, delete and a persistence hierarchy architecture to help you organize your application.

**Fifth step, program to an interface:**

People usually referes the repository to its interface rather than the concrete class, specially on web applications that tend to be modularized as a microservice. To achieve this you could be also implementing a interface to be used in your project, like this:
```
public interface IMyRepository extends IRepository<MyModel, Long> {
}
```
And now your repository could implement it:
```
public class MyModelRepository extends MyBaseRepository<MyModel> implements IMyModelRepository {
    public MyModelRepository(Context context) {
        super(context);
    }
}
```

I don't like much the idea of having to declare the model type in the repository for the ```MyBaseRepository``` and also in the interface for the ```IRepository``` interface, i think it gets a little cumbersome but the freedom to have reference to abstraction and the gain is worth it. I'm also open on how to improve this in the architecture.

## Considerations

This is a basic architecture that i've built researching on how to organize a repository pattern and seeing how other people do it. I'm open to discussion and improment on the subject, so feel free to contact me at anytime.

## What's next

I think that the architecture could be evolved a bit on the Where context to make it easier to build queries. It would be cool if we had a base repository factory that would help on the creation of the repositories so people could depend on its abstraction. That will the creation would be centralized and could use dependency injection to help the test proccess.
