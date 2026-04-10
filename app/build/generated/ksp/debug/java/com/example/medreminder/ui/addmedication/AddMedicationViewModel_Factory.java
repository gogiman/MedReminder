package com.example.medreminder.ui.addmedication;

import androidx.lifecycle.SavedStateHandle;
import com.example.medreminder.data.repository.MedicationRepository;
import com.example.medreminder.notification.AlarmScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AddMedicationViewModel_Factory implements Factory<AddMedicationViewModel> {
  private final Provider<MedicationRepository> repositoryProvider;

  private final Provider<AlarmScheduler> alarmSchedulerProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public AddMedicationViewModel_Factory(Provider<MedicationRepository> repositoryProvider,
      Provider<AlarmScheduler> alarmSchedulerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.alarmSchedulerProvider = alarmSchedulerProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public AddMedicationViewModel get() {
    return newInstance(repositoryProvider.get(), alarmSchedulerProvider.get(), savedStateHandleProvider.get());
  }

  public static AddMedicationViewModel_Factory create(
      Provider<MedicationRepository> repositoryProvider,
      Provider<AlarmScheduler> alarmSchedulerProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new AddMedicationViewModel_Factory(repositoryProvider, alarmSchedulerProvider, savedStateHandleProvider);
  }

  public static AddMedicationViewModel newInstance(MedicationRepository repository,
      AlarmScheduler alarmScheduler, SavedStateHandle savedStateHandle) {
    return new AddMedicationViewModel(repository, alarmScheduler, savedStateHandle);
  }
}
