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
public final class NotificationActionReceiver_MembersInjector implements MembersInjector<NotificationActionReceiver> {
  private final Provider<MedicationRepository> repositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public NotificationActionReceiver_MembersInjector(
      Provider<MedicationRepository> repositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  public static MembersInjector<NotificationActionReceiver> create(
      Provider<MedicationRepository> repositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new NotificationActionReceiver_MembersInjector(repositoryProvider, settingsRepositoryProvider);
  }

  @Override
  public void injectMembers(NotificationActionReceiver instance) {
    injectRepository(instance, repositoryProvider.get());
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.example.medreminder.notification.NotificationActionReceiver.repository")
  public static void injectRepository(NotificationActionReceiver instance,
      MedicationRepository repository) {
    instance.repository = repository;
  }

  @InjectedFieldSignature("com.example.medreminder.notification.NotificationActionReceiver.settingsRepository")
  public static void injectSettingsRepository(NotificationActionReceiver instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }
}
