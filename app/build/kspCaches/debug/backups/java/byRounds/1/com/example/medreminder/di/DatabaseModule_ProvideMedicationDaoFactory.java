package com.example.medreminder.di;

import com.example.medreminder.data.database.AppDatabase;
import com.example.medreminder.data.database.dao.MedicationDao;
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
public final class DatabaseModule_ProvideMedicationDaoFactory implements Factory<MedicationDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideMedicationDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public MedicationDao get() {
    return provideMedicationDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideMedicationDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideMedicationDaoFactory(dbProvider);
  }

  public static MedicationDao provideMedicationDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideMedicationDao(db));
  }
}
