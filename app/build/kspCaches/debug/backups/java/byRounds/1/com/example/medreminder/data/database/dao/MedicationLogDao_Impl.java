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
import com.example.medreminder.data.database.entity.MedicationLog;
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
public final class MedicationLogDao_Impl implements MedicationLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MedicationLog> __insertionAdapterOfMedicationLog;

  private final EntityDeletionOrUpdateAdapter<MedicationLog> __updateAdapterOfMedicationLog;

  private final SharedSQLiteStatement __preparedStmtOfDeleteByMedication;

  public MedicationLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMedicationLog = new EntityInsertionAdapter<MedicationLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `medication_logs` (`id`,`medicationId`,`scheduledTime`,`takenTime`,`status`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MedicationLog entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMedicationId());
        statement.bindLong(3, entity.getScheduledTime());
        if (entity.getTakenTime() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getTakenTime());
        }
        statement.bindString(5, entity.getStatus());
      }
    };
    this.__updateAdapterOfMedicationLog = new EntityDeletionOrUpdateAdapter<MedicationLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `medication_logs` SET `id` = ?,`medicationId` = ?,`scheduledTime` = ?,`takenTime` = ?,`status` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MedicationLog entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getMedicationId());
        statement.bindLong(3, entity.getScheduledTime());
        if (entity.getTakenTime() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getTakenTime());
        }
        statement.bindString(5, entity.getStatus());
        statement.bindLong(6, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteByMedication = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM medication_logs WHERE medicationId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MedicationLog log, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfMedicationLog.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MedicationLog log, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMedicationLog.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteByMedication(final long medId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteByMedication.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, medId);
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
          __preparedStmtOfDeleteByMedication.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MedicationLog>> getLogsForDay(final long startOfDay, final long endOfDay) {
    final String _sql = "\n"
            + "        SELECT * FROM medication_logs \n"
            + "        WHERE scheduledTime >= ? AND scheduledTime < ?\n"
            + "        ORDER BY scheduledTime ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"medication_logs"}, new Callable<List<MedicationLog>>() {
      @Override
      @NonNull
      public List<MedicationLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicationId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicationId");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfTakenTime = CursorUtil.getColumnIndexOrThrow(_cursor, "takenTime");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<MedicationLog> _result = new ArrayList<MedicationLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MedicationLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMedicationId;
            _tmpMedicationId = _cursor.getLong(_cursorIndexOfMedicationId);
            final long _tmpScheduledTime;
            _tmpScheduledTime = _cursor.getLong(_cursorIndexOfScheduledTime);
            final Long _tmpTakenTime;
            if (_cursor.isNull(_cursorIndexOfTakenTime)) {
              _tmpTakenTime = null;
            } else {
              _tmpTakenTime = _cursor.getLong(_cursorIndexOfTakenTime);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new MedicationLog(_tmpId,_tmpMedicationId,_tmpScheduledTime,_tmpTakenTime,_tmpStatus);
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
  public Flow<List<MedicationLog>> getLogsFrom(final long from) {
    final String _sql = "\n"
            + "        SELECT * FROM medication_logs \n"
            + "        WHERE scheduledTime >= ? \n"
            + "        ORDER BY scheduledTime DESC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, from);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"medication_logs"}, new Callable<List<MedicationLog>>() {
      @Override
      @NonNull
      public List<MedicationLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicationId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicationId");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfTakenTime = CursorUtil.getColumnIndexOrThrow(_cursor, "takenTime");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<MedicationLog> _result = new ArrayList<MedicationLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MedicationLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMedicationId;
            _tmpMedicationId = _cursor.getLong(_cursorIndexOfMedicationId);
            final long _tmpScheduledTime;
            _tmpScheduledTime = _cursor.getLong(_cursorIndexOfScheduledTime);
            final Long _tmpTakenTime;
            if (_cursor.isNull(_cursorIndexOfTakenTime)) {
              _tmpTakenTime = null;
            } else {
              _tmpTakenTime = _cursor.getLong(_cursorIndexOfTakenTime);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new MedicationLog(_tmpId,_tmpMedicationId,_tmpScheduledTime,_tmpTakenTime,_tmpStatus);
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
  public Object getByMedAndTime(final long medId, final long scheduledTime,
      final Continuation<? super MedicationLog> $completion) {
    final String _sql = "SELECT * FROM medication_logs WHERE medicationId = ? AND scheduledTime = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, medId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, scheduledTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MedicationLog>() {
      @Override
      @Nullable
      public MedicationLog call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicationId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicationId");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfTakenTime = CursorUtil.getColumnIndexOrThrow(_cursor, "takenTime");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final MedicationLog _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMedicationId;
            _tmpMedicationId = _cursor.getLong(_cursorIndexOfMedicationId);
            final long _tmpScheduledTime;
            _tmpScheduledTime = _cursor.getLong(_cursorIndexOfScheduledTime);
            final Long _tmpTakenTime;
            if (_cursor.isNull(_cursorIndexOfTakenTime)) {
              _tmpTakenTime = null;
            } else {
              _tmpTakenTime = _cursor.getLong(_cursorIndexOfTakenTime);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new MedicationLog(_tmpId,_tmpMedicationId,_tmpScheduledTime,_tmpTakenTime,_tmpStatus);
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

  @Override
  public Object getLogsForDayOnce(final long startOfDay, final long endOfDay,
      final Continuation<? super List<MedicationLog>> $completion) {
    final String _sql = "SELECT * FROM medication_logs WHERE scheduledTime >= ? AND scheduledTime < ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MedicationLog>>() {
      @Override
      @NonNull
      public List<MedicationLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfMedicationId = CursorUtil.getColumnIndexOrThrow(_cursor, "medicationId");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfTakenTime = CursorUtil.getColumnIndexOrThrow(_cursor, "takenTime");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<MedicationLog> _result = new ArrayList<MedicationLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MedicationLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpMedicationId;
            _tmpMedicationId = _cursor.getLong(_cursorIndexOfMedicationId);
            final long _tmpScheduledTime;
            _tmpScheduledTime = _cursor.getLong(_cursorIndexOfScheduledTime);
            final Long _tmpTakenTime;
            if (_cursor.isNull(_cursorIndexOfTakenTime)) {
              _tmpTakenTime = null;
            } else {
              _tmpTakenTime = _cursor.getLong(_cursorIndexOfTakenTime);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new MedicationLog(_tmpId,_tmpMedicationId,_tmpScheduledTime,_tmpTakenTime,_tmpStatus);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
