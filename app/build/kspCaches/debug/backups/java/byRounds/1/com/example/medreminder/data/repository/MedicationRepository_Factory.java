package com.example.medreminder.data.repository;

import com.example.medreminder.data.database.dao.MedicationDao;
import com.example.medreminder.data.database.dao.MedicationLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class MedicationRepository_Factory implements Factory<MedicationRepository> {
  private final Provider<MedicationDao> medicationDaoProvider;

  private final Provider<MedicationLogDao> logDaoProvider;

  public MedicationRepository_Factory(Provider<MedicationDao> medicationDaoProvider,
      Provider<MedicationLogDao> logDaoProvider) {
    this.medicationDaoProvider = medicationDaoProvider;
    this.logDaoProvider = logDaoProvider;
  }

  @Override
  public MedicationRepository get() {
    return newInstance(medicationDaoProvider.get(), logDaoProvider.get());
  }

  public static MedicationRepository_Factory create(Provider<MedicationDao> medicationDaoProvider,
      Provider<MedicationLogDao> logDaoProvider) {
    return new MedicationRepository_Factory(medicationDaoProvider, logDaoProvider);
  }

  public static MedicationRepository newInstance(MedicationDao medicationDao,
      MedicationLogDao logDao) {
    return new MedicationRepository(medicationDao, logDao);
  }
}
