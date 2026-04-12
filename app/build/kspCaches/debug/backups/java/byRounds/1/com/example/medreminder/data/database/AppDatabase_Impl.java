package com.example.medreminder.data.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.example.medreminder.data.database.dao.MedicationDao;
import com.example.medreminder.data.database.dao.MedicationDao_Impl;
import com.example.medreminder.data.database.dao.MedicationLogDao;
import com.example.medreminder.data.database.dao.MedicationLogDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile MedicationDao _medicationDao;

  private volatile MedicationLogDao _medicationLogDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `medications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `dosage` TEXT NOT NULL, `unit` TEXT NOT NULL, `frequencyType` TEXT NOT NULL, `reminderTimes` TEXT NOT NULL, `daysOfWeek` TEXT NOT NULL, `dayReminderTimes` TEXT NOT NULL, `isActive` INTEGER NOT NULL, `notes` TEXT NOT NULL, `color` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `medication_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `medicationId` INTEGER NOT NULL, `scheduledTime` INTEGER NOT NULL, `takenTime` INTEGER, `status` TEXT NOT NULL, FOREIGN KEY(`medicationId`) REFERENCES `medications`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_medication_logs_medicationId` ON `medication_logs` (`medicationId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8a1ad8c38f6217d050784e40d9ca84d5')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `medications`");
        db.execSQL("DROP TABLE IF EXISTS `medication_logs`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsMedications = new HashMap<String, TableInfo.Column>(11);
        _columnsMedications.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedications.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedications.put("dosage", new TableInfo.Column("dosage", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedications.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedications.put("frequencyType", new TableInfo.Column("frequencyType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedications.put("reminderTimes", new TableInfo.Column("reminderTimes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedications.put("daysOfWeek", new TableInfo.Column("daysOfWeek", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedications.put("dayReminderTimes", new TableInfo.Column("dayReminderTimes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedications.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedications.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedications.put("color", new TableInfo.Column("color", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMedications = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMedications = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMedications = new TableInfo("medications", _columnsMedications, _foreignKeysMedications, _indicesMedications);
        final TableInfo _existingMedications = TableInfo.read(db, "medications");
        if (!_infoMedications.equals(_existingMedications)) {
          return new RoomOpenHelper.ValidationResult(false, "medications(com.example.medreminder.data.database.entity.Medication).\n"
                  + " Expected:\n" + _infoMedications + "\n"
                  + " Found:\n" + _existingMedications);
        }
        final HashMap<String, TableInfo.Column> _columnsMedicationLogs = new HashMap<String, TableInfo.Column>(5);
        _columnsMedicationLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicationLogs.put("medicationId", new TableInfo.Column("medicationId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicationLogs.put("scheduledTime", new TableInfo.Column("scheduledTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicationLogs.put("takenTime", new TableInfo.Column("takenTime", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMedicationLogs.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMedicationLogs = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysMedicationLogs.add(new TableInfo.ForeignKey("medications", "CASCADE", "NO ACTION", Arrays.asList("medicationId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesMedicationLogs = new HashSet<TableInfo.Index>(1);
        _indicesMedicationLogs.add(new TableInfo.Index("index_medication_logs_medicationId", false, Arrays.asList("medicationId"), Arrays.asList("ASC")));
        final TableInfo _infoMedicationLogs = new TableInfo("medication_logs", _columnsMedicationLogs, _foreignKeysMedicationLogs, _indicesMedicationLogs);
        final TableInfo _existingMedicationLogs = TableInfo.read(db, "medication_logs");
        if (!_infoMedicationLogs.equals(_existingMedicationLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "medication_logs(com.example.medreminder.data.database.entity.MedicationLog).\n"
                  + " Expected:\n" + _infoMedicationLogs + "\n"
                  + " Found:\n" + _existingMedicationLogs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "8a1ad8c38f6217d050784e40d9ca84d5", "3e40b0827a8829b0adc5461ad2bd90ef");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "medications","medication_logs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `medications`");
      _db.execSQL("DELETE FROM `medication_logs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(MedicationDao.class, MedicationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MedicationLogDao.class, MedicationLogDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public MedicationDao medicationDao() {
    if (_medicationDao != null) {
      return _medicationDao;
    } else {
      synchronized(this) {
        if(_medicationDao == null) {
          _medicationDao = new MedicationDao_Impl(this);
        }
        return _medicationDao;
      }
    }
  }

  @Override
  public MedicationLogDao medicationLogDao() {
    if (_medicationLogDao != null) {
      return _medicationLogDao;
    } else {
      synchronized(this) {
        if(_medicationLogDao == null) {
          _medicationLogDao = new MedicationLogDao_Impl(this);
        }
        return _medicationLogDao;
      }
    }
  }
}
