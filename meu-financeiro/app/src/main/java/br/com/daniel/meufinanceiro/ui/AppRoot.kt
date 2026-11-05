package br.com.daniel.meufinanceiro.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.daniel.meufinanceiro.domain.FinanceCalc
import br.com.daniel.meufinanceiro.domain.Ledger
import br.com.daniel.meufinanceiro.ui.components.AppIcons
import br.com.daniel.meufinanceiro.ui.screens.AnalystScreen
import br.com.daniel.meufinanceiro.ui.screens.BillsScreen
import br.com.daniel.meufinanceiro.ui.screens.BoxDetailScreen
import br.com.daniel.meufinanceiro.ui.screens.BoxesScreen
import br.com.daniel.meufinanceiro.ui.screens.CardScreen
import br.com.daniel.meufinanceiro.ui.screens.ExpenseForm
import br.com.daniel.meufinanceiro.ui.screens.HomeScreen
import br.com.daniel.meufinanceiro.ui.screens.IncomeForm
import br.com.daniel.meufinanceiro.ui.screens.IncomeScreen
import br.com.daniel.meufinanceiro.ui.screens.MoreScreen
import br.com.daniel.meufinanceiro.ui.screens.NfcBatchForm
import br.com.daniel.meufinanceiro.ui.screens.NfcSaleForm
import br.com.daniel.meufinanceiro.ui.screens.NfcScreen
import br.com.daniel.meufinanceiro.ui.screens.ReceiveScreen
import br.com.daniel.meufinanceiro.ui.screens.ReportsScreen
import br.com.daniel.meufinanceiro.ui.screens.SettingsSection
import br.com.daniel.meufinanceiro.ui.screens.SettingsScreen
import br.com.daniel.meufinanceiro.ui.screens.SourceScreen
import br.com.daniel.meufinanceiro.ui.screens.TransactionsScreen
import br.com.daniel.meufinanceiro.ui.screens.TransferForm
import br.com.daniel.meufinanceiro.ui.screens.WorkForm
import br.com.daniel.meufinanceiro.ui.theme.LocalFin

object R {
    const val HOME = "home"; const val TXNS = "txns"; const val INCOME = "income"; const val CARD = "card"; const val MORE = "more"
    const val BOXES = "boxes"; const val BILLS = "bills"; const val REPORTS = "reports"; const val ANALYST = "analyst"
    const val SETTINGS = "settings"; const val NFC = "nfc"; const val TRANSFER = "transfer"; const val RECEIVE = "receive"; const val BATCH = "batch"
    const val ASSISTANT = "assistant"; const val ALERTS = "alerts"; const val LOAN = "loan"; const val LOANS = "loans"
    fun box(id: Long) = "box/$id"
    fun source(id: Long) = "source/$id"
    fun settings(section: String) = "settings/$section"
    fun expense(txn: Long = 0, purchase: Long = 0, bill: Long = 0) = "expense?txn=$txn&purchase=$purchase&bill=$bill"
    fun income(txn: Long = 0, source: Long = 0) = "incomeform?txn=$txn&source=$source"
    fun work(source: Long = 0, id: Long = 0) = "work?source=$source&id=$id"
    fun sale(id: Long = 0) = "sale?id=$id"
}

private data class Tab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
private val tabs = listOf(
    Tab(R.HOME, "Início", AppIcons.Home),
    Tab(R.TXNS, "Extrato", AppIcons.List),
    Tab(R.INCOME, "Trabalhos", AppIcons.Work),
    Tab(R.CARD, "Cartão", AppIcons.Card),
    Tab(R.MORE, "Mais", AppIcons.More),
)

/** Everything a screen needs: data, calculator, actions and navigation. */
class AppState(val vm: MainViewModel, val ledger: Ledger, val calc: FinanceCalc, val nav: NavHostController, val canUseBiometric: Boolean) {
    fun go(route: String) = nav.navigate(route) { launchSingleTop = true }
    fun back() { if (!nav.popBackStack()) nav.navigate(R.HOME) }
}

/** Test hook only (lets UI tests open every route). */
@androidx.annotation.VisibleForTesting
var navForTest: NavHostController? = null

@Composable
fun AppRoot(vm: MainViewModel, ledger: Ledger, canUseBiometric: Boolean) {
    val nav = rememberNavController()
    navForTest = nav
    val calc = remember(ledger) { FinanceCalc(ledger) }
    val state = AppState(vm, ledger, calc, nav, canUseBiometric)
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(Unit) { vm.messages.collect { snackbar.showSnackbar(it) } }
    val entry by nav.currentBackStackEntryAsState()
    val current = entry?.destination?.route
    val showBar = tabs.any { it.route == current }
    val fin = LocalFin.current

    Box(Modifier.fillMaxSize()) {
        androidx.compose.material3.Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (showBar) NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLow, tonalElevation = 0.dp) {
                    tabs.forEach { t ->
                        NavigationBarItem(
                            selected = current == t.route,
                            onClick = {
                                nav.navigate(t.route) {
                                    popUpTo(R.HOME) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(t.icon, contentDescription = t.label) },
                            label = { Text(t.label, maxLines = 1) },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = fin.muted, unselectedTextColor = fin.muted,
                            ),
                        )
                    }
                }
            },
        ) { pad ->
            NavHost(
                nav, startDestination = R.HOME, modifier = Modifier.padding(bottom = pad.calculateBottomPadding()).consumeWindowInsets(pad),
                enterTransition = { if (initialState.destination.route in tabs.map { it.route } && targetState.destination.route in tabs.map { it.route }) fadeIn(tween(180)) else slideInHorizontally(tween(260)) { it / 6 } + fadeIn(tween(220)) },
                exitTransition = { fadeOut(tween(160)) },
                popEnterTransition = { fadeIn(tween(200)) },
                popExitTransition = { slideOutHorizontally(tween(220)) { it / 6 } + fadeOut(tween(180)) },
            ) {
                composable(R.HOME) { HomeScreen(state) }
                composable(R.TXNS) { TransactionsScreen(state) }
                composable(R.INCOME) { IncomeScreen(state) }
                composable(R.CARD) { CardScreen(state) }
                composable(R.MORE) { MoreScreen(state) }
                composable(R.BOXES) { BoxesScreen(state) }
                composable("box/{id}", listOf(navArgument("id") { type = NavType.LongType })) { BoxDetailScreen(state, it.arguments!!.getLong("id")) }
                composable(R.BILLS) { BillsScreen(state) }
                composable(R.REPORTS) { ReportsScreen(state) }
                composable(R.ANALYST) { AnalystScreen(state) }
                composable(R.ASSISTANT) { br.com.daniel.meufinanceiro.ui.screens.AssistantScreen(state) }
                composable(R.ALERTS) { br.com.daniel.meufinanceiro.ui.screens.AlertsScreen(state) }
                composable(R.LOAN) { br.com.daniel.meufinanceiro.ui.screens.LoanForm(state) }
                composable(R.LOANS) { br.com.daniel.meufinanceiro.ui.screens.LoansScreen(state) }
                composable(R.NFC) { NfcScreen(state) }
                composable("source/{id}", listOf(navArgument("id") { type = NavType.LongType })) { SourceScreen(state, it.arguments!!.getLong("id")) }
                composable(R.SETTINGS) { SettingsScreen(state) }
                composable("settings/{section}") { SettingsSection(state, it.arguments?.getString("section") ?: "") }
                composable(R.TRANSFER) { TransferForm(state) }
                composable(R.RECEIVE) { ReceiveScreen(state) }
                composable(R.BATCH) { NfcBatchForm(state) }
                composable(
                    "expense?txn={txn}&purchase={purchase}&bill={bill}",
                    listOf(navArgument("txn") { type = NavType.LongType; defaultValue = 0L }, navArgument("purchase") { type = NavType.LongType; defaultValue = 0L }, navArgument("bill") { type = NavType.LongType; defaultValue = 0L }),
                ) { ExpenseForm(state, it.arguments!!.getLong("txn"), it.arguments!!.getLong("purchase"), it.arguments!!.getLong("bill")) }
                composable(
                    "incomeform?txn={txn}&source={source}",
                    listOf(navArgument("txn") { type = NavType.LongType; defaultValue = 0L }, navArgument("source") { type = NavType.LongType; defaultValue = 0L }),
                ) { IncomeForm(state, it.arguments!!.getLong("txn"), it.arguments!!.getLong("source")) }
                composable(
                    "work?source={source}&id={id}",
                    listOf(navArgument("source") { type = NavType.LongType; defaultValue = 0L }, navArgument("id") { type = NavType.LongType; defaultValue = 0L }),
                ) { WorkForm(state, it.arguments!!.getLong("source"), it.arguments!!.getLong("id")) }
                composable("sale?id={id}", listOf(navArgument("id") { type = NavType.LongType; defaultValue = 0L })) { NfcSaleForm(state, it.arguments!!.getLong("id")) }
            }
        }
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = if (showBar) 84.dp else 16.dp))
    }
}
