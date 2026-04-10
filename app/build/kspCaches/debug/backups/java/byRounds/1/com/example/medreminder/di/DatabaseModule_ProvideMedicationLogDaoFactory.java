package com.example.medreminder.di;

import com.example.medreminder.data.database.AppDatabase;
import com.example.medreminder.data.database.dao.MedicationLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class DatabaseModule_ProvideMedicationLogDaoFactory implements Factory<MedicationLogDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideMedicationLogDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public MedicationLogDao get() {
    return provideMedicationLogDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideMedicationLogDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideMedicationLogDaoFactory(dbProvider);
  }

  public static MedicationLogDao provideMedicationLogDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMedicationLogDao(db));
  }
}
