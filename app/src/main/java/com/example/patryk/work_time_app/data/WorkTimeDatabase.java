package com.example.patryk.work_time_app.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {WorkTimeRecord.class, PauseTimeRecord.class}, version = 1, exportSchema = false)
@TypeConverters(value = Converters.class)
abstract class WorkTimeDatabase extends RoomDatabase {

    private static volatile WorkTimeDatabase INSTANCE;

    public abstract WorkTimeDAO workTimeDAO();

    public abstract PauseTimeDAO pauseTimeDAO();

    private static final int NUMBER_OF_THREADS = 2;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase sqLiteDatabase) {
            super.onOpen(sqLiteDatabase);
        }
    };

    static WorkTimeDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WorkTimeDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), WorkTimeDatabase.class, "database").build();
                }
            }
        }
        return INSTANCE;
    }

}
