package com.example.medreminder;

import com.example.medreminder.data.preferences.SettingsRepository;
import com.example.medreminder.data.repository.MedicationRepository;
import com.example.medreminder.notification.AlarmScheduler;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MedReminderApp_MembersInjector implements MembersInjector<MedReminderApp> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<MedicationRepository> repositoryProvider;

  private final Provider<AlarmScheduler> alarmSchedulerProvider;

  public MedReminderApp_MembersInjector(Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<MedicationRepository> repositoryProvider,
      Provider<AlarmScheduler> alarmSchedulerProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.repositoryProvider = repositoryProvider;
    this.alarmSchedulerProvider = alarmSchedulerProvider;
  }

  public static MembersInjector<MedReminderApp> create(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<MedicationRepository> repositoryProvider,
      Provider<AlarmScheduler> alarmSchedulerProvider) {
    return new MedReminderApp_MembersInjector(settingsRepositoryProvider, repositoryProvider, alarmSchedulerProvider);
  }

  @Override
  public void injectMembers(MedReminderApp instance) {
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
    injectRepository(instance, repositoryProvider.get());
    injectAlarmScheduler(instance, alarmSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.example.medreminder.MedReminderApp.settingsRepository")
  public static void injectSettingsRepository(MedReminderApp instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }

  @InjectedFieldSignature("com.example.medreminder.MedReminderApp.repository")
  public static void injectRepository(MedReminderApp instance, MedicationRepository repository) {
    instance.repository = repository;
  }

  @InjectedFieldSignature("com.example.medreminder.MedReminderApp.alarmScheduler")
  public static void injectAlarmScheduler(MedReminderApp instance, AlarmScheduler alarmScheduler) {
    instance.alarmScheduler = alarmScheduler;
  }
}
