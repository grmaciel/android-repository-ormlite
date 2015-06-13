package br.com.persistence.repository;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.TableUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import br.com.persistence.AbstractDBHelper;

/**
 * Created by gilson.maciel on 27/04/2015.
 */
public abstract class BaseOrmLiteRepository<T, Id> implements IRepository<T, Id> {
    private static Class<? extends AbstractDBHelper> helperClass;
    protected static Context context;
    private Class<T> genericType;
    private Where<T, Id> where;
    private static AbstractDBHelper instance;


    public BaseOrmLiteRepository(Context context, Class<? extends AbstractDBHelper> helperClazz) {
        this.context = context;
        this.helperClass = helperClazz;

        try {
            getModelClass();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getModelClass() throws ClassNotFoundException {
        if (genericType == null) {
            Type mySuperclass = getClass().getGenericSuperclass();
            Type tType = ((ParameterizedType) mySuperclass).getActualTypeArguments()[0];
            String className = tType.toString().split(" ")[1];
            genericType = (Class<T>) Class.forName(className);
            Log.d(BaseOrmLiteRepository.class.getSimpleName(),
                    "Type class: " + genericType.getSimpleName());
        }
    }

    @Override
    public void save(T entity) throws SQLException {
        getDao().createOrUpdate(entity);
    }

    @Override
    public void saveBatch(final List<T> entities) throws Exception {
        getDao().callBatchTasks(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (T entity : entities) {
                    getDao().createOrUpdate(entity);
                }
                return null;
            }
        });
    }

    @Override
    public List<T> queryAll() throws SQLException {
        return getDao().queryForAll();
    }

    @Override
    public T findById(Id id) throws SQLException {
        return getDao().queryForId(id);
    }

    @Override
    public void delete(T entity) throws SQLException {
        getDao().delete(entity);
    }

    public Dao<T, Id> getDao() throws SQLException {
        return DaoManager.createDao(getPersistenceManager().getConnectionSource(),
                genericType);
    }

    public QueryBuilder<T, Id> getQueryBuilder() throws SQLException {
        return getDao().queryBuilder();
    }

    protected Where<T, Id> getWhere() throws SQLException {
        return getDao().queryBuilder().where();
    }

    public T getEntitySimpleWhere(String field, Object value) throws SQLException {
        Where<T, Id> where = getWhere();
        where.eq(field, value);
        return where.queryForFirst();
    }

    protected T getEntityWithWhereAND(Map<String, Object> values) throws SQLException {
        Where<T, Id> where = getWhere();

        Iterator entries = values.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry) entries.next();
            where.eq(entry.getKey(), entry.getValue());

            if (entries.hasNext()) {
                where.and();
            }
        }

        return where.queryForFirst();
    }

    public void clear() throws SQLException {
        TableUtils.clearTable(getPersistenceManager().getConnectionSource(), genericType);
    }

    public void beginTransation() {
        getPersistenceManager().getWritableDatabase().beginTransaction();
    }

    public void commitTransaction() {
        getPersistenceManager().getWritableDatabase().setTransactionSuccessful();
    }

    public void endTransaction() {
        getPersistenceManager().getWritableDatabase().endTransaction();
    }

    protected static AbstractDBHelper getPersistenceManager() {
        if (instance == null) {
            try {
                Log.d("", "CREATING PERSISTENCE MANAGER");
                instance = helperClass.getDeclaredConstructor(Context.class)
                        .newInstance(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return instance;
    }
}
