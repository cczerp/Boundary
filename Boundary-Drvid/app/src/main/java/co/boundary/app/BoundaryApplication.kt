package co.boundary.app

import androidx.lifecycle.ProcessLifecycleOwner
import cash.z.ecc.android.sdk.Synchronizer
import co.boundary.app.crash.android.GlobalCrashReporter
import co.boundary.app.crash.android.di.CrashReportersProvider
import co.boundary.app.crash.android.di.crashProviderModule
import co.boundary.app.di.addressBookModule
import co.boundary.app.di.coreModule
import co.boundary.app.di.dataSourceModule
import co.boundary.app.di.mapperModule
import co.boundary.app.di.metadataModule
import co.boundary.app.di.providerModule
import co.boundary.app.di.repositoryModule
import co.boundary.app.di.useCaseModule
import co.boundary.app.di.viewModelModule
import co.boundary.app.spackle.StrictModeCompat
import co.boundary.app.spackle.Twig
import co.boundary.app.ui.common.provider.CrashReportingStorageProvider
import co.boundary.app.ui.common.provider.SynchronizerProvider
import co.boundary.app.ui.common.repository.ApplicationStateRepository
import co.boundary.app.ui.common.repository.FlexaRepository
import co.boundary.app.ui.common.repository.HomeMessageCacheRepository
import co.boundary.app.ui.common.repository.WalletSnapshotRepository
import co.boundary.app.ui.screen.error.ErrorArgs
import co.boundary.app.ui.screen.error.NavigateToErrorUseCase
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf

class BoundaryApplication : CoroutineApplication() {
    private val flexaRepository by inject<FlexaRepository>()
    private val getAvailableCrashReporters: CrashReportersProvider by inject()
    private val homeMessageCacheRepository: HomeMessageCacheRepository by inject()
    private val walletSnapshotRepository: WalletSnapshotRepository by inject()
    private val crashReportingStorageProvider: CrashReportingStorageProvider by inject()
    private val applicationStateRepository: ApplicationStateRepository by inject {
        parametersOf(ProcessLifecycleOwner.get().lifecycle)
    }
    private val synchronizerProvider: SynchronizerProvider by inject()
    private val navigateToError: NavigateToErrorUseCase by inject()

    override fun onCreate() {
        super.onCreate()

        configureLogging()

        configureStrictMode()

        startKoin {
            androidLogger()
            androidContext(this@BoundaryApplication)
            modules(
                coreModule,
                providerModule,
                crashProviderModule,
                dataSourceModule,
                repositoryModule,
                addressBookModule,
                metadataModule,
                useCaseModule,
                mapperModule,
                viewModelModule
            )
        }

        // Since analytics will need disk IO internally, we want this to be registered after strict
        // mode is configured to ensure none of that IO happens on the main thread
        configureAnalytics()

        flexaRepository.init()
        homeMessageCacheRepository.init()
        walletSnapshotRepository.init()
        applicationStateRepository.init()
        observeSynchronizerError()
    }

    private fun observeSynchronizerError() {
        applicationScope.launch {
            synchronizerProvider.synchronizer
                .map { it?.initializationError }
                .collect {
                    if (it == Synchronizer.InitializationError.TOR_NOT_AVAILABLE) {
                        navigateToError(ErrorArgs.SynchronizerTorInitError)
                    }
                }
        }
    }

    private fun configureLogging() {
        Twig.initialize(applicationContext)
        Twig.info { "Starting application…" }

        if (!BuildConfig.DEBUG) {
            // In release builds, logs should be stripped by R8 rules
            Twig.assertLoggingStripped()
        }
    }

    private fun configureStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictModeCompat.enableStrictMode(BuildConfig.IS_STRICT_MODE_CRASH_ENABLED)
        }
    }

    private fun configureAnalytics() {
        if (GlobalCrashReporter.register(this, getAvailableCrashReporters())) {
            applicationScope.launch {
                crashReportingStorageProvider.observe().collect {
                    Twig.debug { "Is crashlytics enabled: $it" }
                    if (it == true) {
                        GlobalCrashReporter.enable()
                    } else {
                        GlobalCrashReporter.disableAndDelete()
                    }
                }
            }
        }
    }
}
