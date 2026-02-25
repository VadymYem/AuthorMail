package top.authorche.authormail.ui.navigation
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import top.authorche.authormail.ui.screen.*

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    NavHost(nav, startDestination = "main") {
        composable("main")    { MainScreen(   onSettings = { nav.navigate("settings") }, onAbout = { nav.navigate("about") }) }
        composable("settings"){ AiSettingsScreen(onBack  = { nav.popBackStack() }) }
        composable("about")   { AboutScreen(  onBack      = { nav.popBackStack() }) }
        composable("account") { AccountSetupScreen(onBack = { nav.popBackStack() }, onDone = { nav.popBackStack() }) }
    }
}
