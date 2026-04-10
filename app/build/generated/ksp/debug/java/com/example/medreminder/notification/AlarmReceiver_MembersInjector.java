package com.example.medreminder.notification;

import com.example.medreminder.data.preferences.SettingsRepository;
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
public final class AlarmReceiver_MembersInjector implements MembersInjector<AlarmReceiver> {
  private final Provider<MedicationRepository> repositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public AlarmReceiver_MembersInjector(Provider<MedicationRepository> repositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  public static MembersInjector<AlarmReceiver> create(
      Provider<MedicationRepository> repositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new AlarmReceiver_MembersInjector(repositoryProvider, settingsRepositoryProvider);
  }

  @Override
  public void injectMembers(AlarmReceiver instance) {
    injectRepository(instance, repositoryProvider.get());
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.example.medreminder.notification.AlarmReceiver.repository")
  public static void injectRepository(AlarmReceiver instance, MedicationRepository repository) {
    instance.repository = repository;
  }

  @InjectedFieldSignature("com.example.medreminder.notification.AlarmReceiver.settingsRepository")
  public static void injectSettingsRepository(AlarmReceiver instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }
}
