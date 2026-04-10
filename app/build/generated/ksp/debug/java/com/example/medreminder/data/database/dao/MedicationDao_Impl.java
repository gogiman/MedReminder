package com.example.medreminder.data.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.medreminder.data.database.entity.Medication;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MedicationDao_Impl implements MedicationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Medication> __insertionAdapterOfMedication;

  private final EntityDeletionOrUpdateAdapter<Medication> __updateAdapterOfMedication;

  private final SharedSQLiteStatement __preparedStmtOfSoftDelete;

  private final SharedSQLiteStatement __preparedStmtOfHardDelete;

  private final SharedSQLiteStatement __preparedStmtOfSetEnabled;

  public MedicationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMedication = new EntityInsertionAdapter<Medication>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `medications` (`id`,`name`,`dosage`,`unit`,`frequencyType`,`reminderTimes`,`daysOfWeek`,`isActive`,`notes`,`color`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Medication entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDosage());
        statement.bindString(4, entity.getUnit());
        statement.bindString(5, entity.getFrequencyType());
        statement.bindString(6, entity.getReminderTimes());
        statement.bindString(7, entity.getDaysOfWeek());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindString(9, entity.getNotes());
        statement.bindLong(10, entity.getColor());
      }
    };
    this.__updateAdapterOfMedication = new EntityDeletionOrUpdateAdapter<Medication>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `medications` SET `id` = ?,`name` = ?,`dosage` = ?,`unit` = ?,`frequencyType` = ?,`reminderTimes` = ?,`daysOfWeek` = ?,`isActive` = ?,`notes` = ?,`color` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Medication entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDosage());
        statement.bindString(4, entity.getUnit());
        statement.bindString(5, entity.getFrequencyType());
        statement.bindString(6, entity.getReminderTimes());
        statement.bindString(7, entity.getDaysOfWeek());
        final int _tmp = entity.isActive() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindString(9, entity.getNotes());
        statement.bindLong(10, entity.getColor());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfSoftDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE medications SET isActive = 0 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfHardDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM medications WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetEnabled = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE medications SET isActive = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Medication medication, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMedication.insertAndReturnId(medication);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final Medication medication, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMedication.handle(medication);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object softDelete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSoftDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSoftDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object hardDelete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfHardDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfHardDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setEnabled(final long id, final boolean enabled,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetEnabled.acquire();
        int _argIndex = 1;
        final int _tmp = enabled ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSetEnabled.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Medication>> getAllActive() {
    final String _sql = "SELECT * FROM medications WHERE isActive = 1 ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"medications"}, new Callable<List<Medication>>() {
      @Override
      @NonNull
      public List<Medication> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDosage = CursorUtil.getColumnIndexOrThrow(_cursor, "dosage");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfFrequencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyType");
          final int _cursorIndexOfReminderTimes = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTimes");
          final int _cursorIndexOfDaysOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "daysOfWeek");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final List<Medication> _result = new ArrayList<Medication>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Medication _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDosage;
            _tmpDosage = _cursor.getString(_cursorIndexOfDosage);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpFrequencyType;
            _tmpFrequencyType = _cursor.getString(_cursorIndexOfFrequencyType);
            final String _tmpReminderTimes;
            _tmpReminderTimes = _cursor.getString(_cursorIndexOfReminderTimes);
            final String _tmpDaysOfWeek;
            _tmpDaysOfWeek = _cursor.getString(_cursorIndexOfDaysOfWeek);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final int _tmpColor;
            _tmpColor = _cursor.getInt(_cursorIndexOfColor);
            _item = new Medication(_tmpId,_tmpName,_tmpDosage,_tmpUnit,_tmpFrequencyType,_tmpReminderTimes,_tmpDaysOfWeek,_tmpIsActive,_tmpNotes,_tmpColor);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Medication>> getAll() {
    final String _sql = "SELECT * FROM medications ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"medications"}, new Callable<List<Medication>>() {
      @Override
      @NonNull
      public List<Medication> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDosage = CursorUtil.getColumnIndexOrThrow(_cursor, "dosage");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfFrequencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyType");
          final int _cursorIndexOfReminderTimes = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTimes");
          final int _cursorIndexOfDaysOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "daysOfWeek");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final List<Medication> _result = new ArrayList<Medication>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Medication _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDosage;
            _tmpDosage = _cursor.getString(_cursorIndexOfDosage);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpFrequencyType;
            _tmpFrequencyType = _cursor.getString(_cursorIndexOfFrequencyType);
            final String _tmpReminderTimes;
            _tmpReminderTimes = _cursor.getString(_cursorIndexOfReminderTimes);
            final String _tmpDaysOfWeek;
            _tmpDaysOfWeek = _cursor.getString(_cursorIndexOfDaysOfWeek);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final int _tmpColor;
            _tmpColor = _cursor.getInt(_cursorIndexOfColor);
            _item = new Medication(_tmpId,_tmpName,_tmpDosage,_tmpUnit,_tmpFrequencyType,_tmpReminderTimes,_tmpDaysOfWeek,_tmpIsActive,_tmpNotes,_tmpColor);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAllActiveOnce(final Continuation<? super List<Medication>> $completion) {
    final String _sql = "SELECT * FROM medications WHERE isActive = 1 ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Medication>>() {
      @Override
      @NonNull
      public List<Medication> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDosage = CursorUtil.getColumnIndexOrThrow(_cursor, "dosage");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfFrequencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyType");
          final int _cursorIndexOfReminderTimes = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTimes");
          final int _cursorIndexOfDaysOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "daysOfWeek");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final List<Medication> _result = new ArrayList<Medication>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Medication _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDosage;
            _tmpDosage = _cursor.getString(_cursorIndexOfDosage);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpFrequencyType;
            _tmpFrequencyType = _cursor.getString(_cursorIndexOfFrequencyType);
            final String _tmpReminderTimes;
            _tmpReminderTimes = _cursor.getString(_cursorIndexOfReminderTimes);
            final String _tmpDaysOfWeek;
            _tmpDaysOfWeek = _cursor.getString(_cursorIndexOfDaysOfWeek);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final int _tmpColor;
            _tmpColor = _cursor.getInt(_cursorIndexOfColor);
            _item = new Medication(_tmpId,_tmpName,_tmpDosage,_tmpUnit,_tmpFrequencyType,_tmpReminderTimes,_tmpDaysOfWeek,_tmpIsActive,_tmpNotes,_tmpColor);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super Medication> $completion) {
    final String _sql = "SELECT * FROM medications WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Medication>() {
      @Override
      @Nullable
      public Medication call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDosage = CursorUtil.getColumnIndexOrThrow(_cursor, "dosage");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfFrequencyType = CursorUtil.getColumnIndexOrThrow(_cursor, "frequencyType");
          final int _cursorIndexOfReminderTimes = CursorUtil.getColumnIndexOrThrow(_cursor, "reminderTimes");
          final int _cursorIndexOfDaysOfWeek = CursorUtil.getColumnIndexOrThrow(_cursor, "daysOfWeek");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfColor = CursorUtil.getColumnIndexOrThrow(_cursor, "color");
          final Medication _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDosage;
            _tmpDosage = _cursor.getString(_cursorIndexOfDosage);
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            final String _tmpFrequencyType;
            _tmpFrequencyType = _cursor.getString(_cursorIndexOfFrequencyType);
            final String _tmpReminderTimes;
            _tmpReminderTimes = _cursor.getString(_cursorIndexOfReminderTimes);
            final String _tmpDaysOfWeek;
            _tmpDaysOfWeek = _cursor.getString(_cursorIndexOfDaysOfWeek);
            final boolean _tmpIsActive;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp != 0;
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final int _tmpColor;
            _tmpColor = _cursor.getInt(_cursorIndexOfColor);
            _result = new Medication(_tmpId,_tmpName,_tmpDosage,_tmpUnit,_tmpFrequencyType,_tmpReminderTimes,_tmpDaysOfWeek,_tmpIsActive,_tmpNotes,_tmpColor);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
