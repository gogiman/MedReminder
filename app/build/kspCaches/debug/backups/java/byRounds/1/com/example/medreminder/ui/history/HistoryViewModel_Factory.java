package com.example.medreminder.ui.history;

import com.example.medreminder.data.repository.MedicationRepository;
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
public final class HistoryViewModel_Factory implements Factory<HistoryViewModel> {
  private final Provider<MedicationRepository> repositoryProvider;

  public HistoryViewModel_Factory(Provider<MedicationRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public HistoryViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static HistoryViewModel_Factory create(Provider<MedicationRepository> repositoryProvider) {
    return new HistoryViewModel_Factory(repositoryProvider);
  }

  public static HistoryViewModel newInstance(MedicationRepository repository) {
    return new HistoryViewModel(repository);
  }
}
