package com.example.medreminder.notification;

import com.example.medreminder.data.repository.MedicationRepository;
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
public final class BootReceiver_MembersInjector implements MembersInjector<BootReceiver> {
  private final Provider<MedicationRepository> repositoryProvider;

  private final Provider<AlarmScheduler> alarmSchedulerProvider;

  public BootReceiver_MembersInjector(Provider<MedicationRepository> repositoryProvider,
      Provider<AlarmScheduler> alarmSchedulerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.alarmSchedulerProvider = alarmSchedulerProvider;
  }

  public static MembersInjector<BootReceiver> create(
      Provider<MedicationRepository> repositoryProvider,
      Provider<AlarmScheduler> alarmSchedulerProvider) {
    return new BootReceiver_MembersInjector(repositoryProvider, alarmSchedulerProvider);
  }

  @Override
  public void injectMembers(BootReceiver instance) {
    injectRepository(instance, repositoryProvider.get());
    injectAlarmScheduler(instance, alarmSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.example.medreminder.notification.BootReceiver.repository")
  public static void injectRepository(BootReceiver instance, MedicationRepository repository) {
    instance.repository = repository;
  }

  @InjectedFieldSignature("com.example.medreminder.notification.BootReceiver.alarmScheduler")
  public static void injectAlarmScheduler(BootReceiver instance, AlarmScheduler alarmScheduler) {
    instance.alarmScheduler = alarmScheduler;
  }
}
