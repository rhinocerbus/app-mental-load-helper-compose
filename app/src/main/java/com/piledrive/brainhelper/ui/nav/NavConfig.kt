package com.piledrive.brainhelper.ui.nav

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.piledrive.brainhelper.ui.screens.SplashScreen
import com.piledrive.brainhelper.ui.screens.auth.AuthScreen
import com.piledrive.brainhelper.ui.screens.main.MainScreen
import com.piledrive.brainhelper.ui.screens.scratch.ScratchPadScreen
import com.piledrive.brainhelper.viewmodel.AuthViewModel
import com.piledrive.brainhelper.viewmodel.HomeViewModel
import com.piledrive.brainhelper.viewmodel.ScratchPadViewModel
import com.piledrive.brainhelper.viewmodel.SplashViewModel
import kotlinx.coroutines.channels.consumeEach

interface NavRoute {
	val routeValue: String
}

enum class TopLevelRoutes(override val routeValue: String) : NavRoute {
	SPLASH("splash"), AUTH("auth"), HOME("home"), SCRATCH("scratch")
}

enum class NavArgKeys(val key: String) { GUID("guid") }

enum class ChildRoutes(override val routeValue: String) : NavRoute {
	CONTENT_DETAILS("content/{${NavArgKeys.GUID.key}}"),
}

@Composable
fun RootNavHost() {
	val navController = rememberNavController()
	NavHost(
		modifier = Modifier.safeDrawingPadding(),
		navController = navController,
		startDestination = SplashScreen.routeValue
	) {
		/*
		val podcastCallbacks = object : PodcastCallbacks {
			override val onPodcastOpen: (podcast: IPodcastData) -> Unit = { podcast ->
				val toRoute = PodcastScreen.routeValue.replace("{${NavArgKeys.GUID.key}}", podcast.guid)
				navController.navigate(toRoute)
			}
			override val onFollowToggled: (podcast: IPodcastData, status: PodcastFollowStatus) -> Unit = { _, _ -> }
			override val onShowFullDescription: (podcast: IPodcastData) -> Unit = {}
			override val onOpenShowSettings: (podcast: IPodcastData) -> Unit = {}
		}
*/

		composable(route = SplashScreen.routeValue) {
			val viewModel: SplashViewModel = hiltViewModel<SplashViewModel>()
			LaunchedEffect("cached auth status") {
				viewModel.authorizedEvent.consumeEach {
					val toRoute = if (it) {
						MainScreen.routeValue
					} else {
						AuthScreen.routeValue
					}
					navController.navigate(toRoute) {
						popUpTo(navController.graph.id) {
							inclusive = true
						}
					}
				}
			}
			SplashScreen.draw(
				viewModel,
			)
		}

		composable(route = AuthScreen.routeValue) {
			val viewModel: AuthViewModel = hiltViewModel<AuthViewModel>()
			LaunchedEffect("auth status") {
				viewModel.coordinator.loginSuccessEvent.consumeEach {
					if (it) {
						val toRoute = MainScreen.routeValue
						navController.popBackStack()
						navController.navigate(toRoute) {
							popUpTo(navController.graph.id) {
								inclusive = true
							}
						}
					} else {
						//?
					}
				}
			}
			AuthScreen.draw(
				viewModel.coordinator
			)
		}

		composable(route = MainScreen.routeValue) {
			val viewModel: HomeViewModel = hiltViewModel<HomeViewModel>()
			LaunchedEffect("watch_logout") {
				viewModel.loggedOutEvent.consumeEach {
					if (it) {
						val toRoute = AuthScreen.routeValue
						navController.popBackStack()
						navController.navigate(toRoute) {
							popUpTo(navController.graph.id) {
								inclusive = true
							}
						}
					} else {
						//?
					}
				}
			}

			MainScreen.draw(
				viewModel.barCoordinator,
				viewModel.mainScreenCoordinator,
				object : MainScreen.MainScreenNavCallbacks {
					override val onLaunchScratchPad: () -> Unit = {
						navController.navigate(TopLevelRoutes.SCRATCH.routeValue) {
						}
					}
				}
			)
		}

		composable(TopLevelRoutes.SCRATCH.routeValue) {
			val viewModel: ScratchPadViewModel = hiltViewModel<ScratchPadViewModel>()
			ScratchPadScreen.draw(viewModel.coordinator)
		}
		/*
		composable(route = PodcastScreen.routeValue) { navStackEntry ->
			val podcastGuid = navStackEntry.arguments?.getString(NavArgKeys.GUID.key)
				?: throw InvalidParameterException("no podcast guid provided for nav")
			val podcastViewModel: PodcastViewModel = hiltViewModel<PodcastViewModel>()
			//val vmOwner = LocalViewModelStoreOwner.current ?: return@composable
			//val altPodcastViewModel: PodcastViewModel = hiltViewModel<PodcastViewModel>(vmOwner)
			val playerViewModel = hiltViewModel<PlayerViewModel>()
			LaunchedEffect("load_podcast_on_nav") {
				podcastViewModel.loadPodcast(podcastGuid)
			}
			PodcastScreen.draw(
				podcastViewModel,
				playerViewModel,
				podcastGuid,
				onBack = { navController.navigateUp() },
				onOpenPodcastSettings = { podcast ->
					val toRoute = PodcastSettingsScreen.routeValue.replace("{${NavArgKeys.GUID.key}}", podcast.guid)
					navController.navigate(toRoute)
				}
			)
		}
		 */
	}
}

