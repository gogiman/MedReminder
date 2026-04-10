package com.example.medreminder.ui.home;

import android.content.Context;
import com.example.medreminder.data.preferences.SettingsRepository;
import com.example.medreminder.data.repository.MedicationRepository;
import com.example.medreminder.notification.AlarmScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<MedicationRepository> repositoryProvider;

  private final Provider<AlarmScheduler> alarmSchedulerProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<Context> contextProvider;

  public HomeViewModel_Factory(Provider<MedicationRepository> repositoryProvider,
      Provider<AlarmScheduler> alarmSchedulerProvider,
      Provider<SettingsRepository> settingsRepositoryProvider, Provider<Context> contextProvider) {
    this.repositoryProvider = repositoryProvider;
    this.alarmSchedulerProvider = alarmSchedulerProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repositoryProvider.get(), alarmSchedulerProvider.get(), settingsRepositoryProvider.get(), contextProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<MedicationRepository> repositoryProvider,
      Provider<AlarmScheduler> alarmSchedulerProvider,
      Provider<SettingsRepository> settingsRepositoryProvider, Provider<Context> contextProvider) {
    return new HomeViewModel_Factory(repositoryProvider, alarmSchedulerProvider, settingsRepositoryProvider, contextProvider);
  }

  public static HomeViewModel newInstance(MedicationRepository repository,
      AlarmScheduler alarmScheduler, SettingsRepository settingsRepository, Context context) {
    return new HomeViewModel(repository, alarmScheduler, settingsRepository, context);
  }
}
