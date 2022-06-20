package app.accrescent.client.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import app.accrescent.client.R
import app.accrescent.client.data.InstallStatus
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun InstalledAppsScreen(
    navController: NavController,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    padding: PaddingValues,
    viewModel: AppListViewModel = viewModel(),
) {
    val apps by viewModel.apps.collectAsState(emptyList())
    val installStatuses = viewModel.installStatuses
    val installedApps = apps.filter {
        when (installStatuses[it.id]) {
            InstallStatus.INSTALLED, InstallStatus.UPDATABLE -> true
            else -> false
        }
    }

    SwipeRefresh(
        modifier = Modifier.padding(padding),
        state = rememberSwipeRefreshState(viewModel.isRefreshing),
        onRefresh = {
            viewModel.refreshRepoData()
            viewModel.refreshInstallStatuses()
        },
    ) {
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            if (apps.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.swipe_refresh),
                        Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            } else if (installedApps.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.no_apps_installed),
                        Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                item { Spacer(Modifier.height(16.dp)) }
                items(installedApps, key = { app -> app.id }) { app ->
                    InstallableAppCard(
                        app = app,
                        navController = navController,
                        installStatus = installStatuses[app.id] ?: InstallStatus.INSTALLABLE,
                        onInstallClicked = viewModel::installApp,
                        onOpenClicked = viewModel::openApp,
                    )
                }
            }
        }

        if (viewModel.error != null) {
            LaunchedEffect(scaffoldState.snackbarHostState) {
                scaffoldState.snackbarHostState.showSnackbar(message = viewModel.error!!)
            }
        }
    }
}