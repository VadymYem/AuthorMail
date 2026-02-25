package top.authorche.authormail.ui.screen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import top.authorche.authormail.domain.model.Email
import top.authorche.authormail.domain.model.EmailFolder
import top.authorche.authormail.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

private data class NavItem(val folder: EmailFolder, val label: String, val icon: ImageVector, val iconSel: ImageVector)
private val NAV = listOf(
    NavItem(EmailFolder.INBOX,  "Inbox",   Icons.Outlined.Inbox,   Icons.Filled.Inbox),
    NavItem(EmailFolder.SENT,   "Sent",    Icons.Outlined.Send,    Icons.Filled.Send),
    NavItem(EmailFolder.DRAFTS, "Drafts",  Icons.Outlined.Drafts,  Icons.Filled.Drafts),
    NavItem(EmailFolder.SPAM,   "Spam",    Icons.Outlined.ReportGmailerrorred, Icons.Filled.ReportGmailerrorred),
    NavItem(EmailFolder.TRASH,  "Trash",   Icons.Outlined.Delete,  Icons.Filled.Delete),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: MainViewModel = hiltViewModel(), onSettings: () -> Unit, onAbout: () -> Unit) {
    val state by vm.state.collectAsState()
    Row(Modifier.fillMaxSize()) {
        NavigationRail(Modifier.fillMaxHeight(), header = {
            Spacer(Modifier.height(8.dp))
            FloatingActionButton({}, containerColor = MaterialTheme.colorScheme.primaryContainer,
                elevation = FloatingActionButtonDefaults.loweredElevation()) {
                Icon(Icons.Filled.Edit, "Compose")
            }
        }) {
            NAV.forEach { item ->
                val sel = state.folder == item.folder
                NavigationRailItem(sel, { vm.selectFolder(item.folder) },
                    { Icon(if (sel) item.iconSel else item.icon, item.label) },
                    label = { Text(item.label) })
            }
            Spacer(Modifier.weight(1f))
            NavigationRailItem(false, onSettings, { Icon(Icons.Outlined.Settings,"Settings") }, label={Text("Settings")})
            NavigationRailItem(false, onAbout,    { Icon(Icons.Outlined.Person,"About") },    label={Text("About")})
            Spacer(Modifier.height(8.dp))
        }
        Scaffold(Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(title = { Text(state.folder.name.lowercase().replaceFirstChar{it.uppercase()},
                    style=MaterialTheme.typography.titleLarge) },
                    actions = {
                        IconButton({}) { Icon(Icons.Outlined.Search,"Search") }
                        IconButton({}) { Icon(Icons.Outlined.Refresh,"Refresh") }
                    })
            }) { pad ->
            val filtered = state.emails.filter { it.folder == state.folder }
            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(pad), Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.MarkEmailRead, null, Modifier.size(64.dp), tint=MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(12.dp))
                        Text("No emails", color=MaterialTheme.colorScheme.outline)
                    }
                }
            } else {
                LazyColumn(Modifier.padding(pad), contentPadding=PaddingValues(vertical=4.dp),
                    verticalArrangement=Arrangement.spacedBy(2.dp)) {
                    items(filtered, key={it.id}) { EmailCard(it) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmailCard(email: Email) {
    val isSpam = (email.spamScore ?: 0f) >= 0.75f
    Card(onClick={}, Modifier.fillMaxWidth().padding(horizontal=8.dp, vertical=2.dp),
        shape=RoundedCornerShape(12.dp),
        colors=CardDefaults.cardColors(containerColor=if(!email.isRead)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha=0.25f)
            else MaterialTheme.colorScheme.surface),
        elevation=CardDefaults.cardElevation(1.dp)) {
        Row(Modifier.padding(16.dp,12.dp), verticalAlignment=Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(CircleShape).background(avatarColor(email.fromName)),
                contentAlignment=Alignment.Center) {
                Text(email.fromName.take(1).uppercase(), color=Color.White, fontWeight=FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment=Alignment.CenterVertically) {
                    Text(email.fromName, style=MaterialTheme.typography.titleSmall,
                        fontWeight=if(!email.isRead) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines=1, overflow=TextOverflow.Ellipsis, modifier=Modifier.weight(1f))
                    if(isSpam) {
                        Spacer(Modifier.width(4.dp))
                        Surface(shape=RoundedCornerShape(4.dp),
                            color=MaterialTheme.colorScheme.errorContainer) {
                            Text("AI Spam", Modifier.padding(4.dp,2.dp),
                                style=MaterialTheme.typography.labelSmall,
                                color=MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(fmtTime(email.timestamp), style=MaterialTheme.typography.labelSmall,
                        color=MaterialTheme.colorScheme.outline)
                }
                Text(email.subject, style=MaterialTheme.typography.bodyMedium,
                    fontWeight=if(!email.isRead) FontWeight.Medium else FontWeight.Normal,
                    maxLines=1, overflow=TextOverflow.Ellipsis)
                Text(email.snippet, style=MaterialTheme.typography.bodySmall,
                    color=MaterialTheme.colorScheme.onSurfaceVariant, maxLines=2, overflow=TextOverflow.Ellipsis)
                email.spamReason?.takeIf{isSpam}?.let {
                    Text("⚠ $it", style=MaterialTheme.typography.labelSmall,
                        color=MaterialTheme.colorScheme.error, maxLines=1, overflow=TextOverflow.Ellipsis)
                }
            }
        }
    }
}

private fun avatarColor(n:String):Color{val c=listOf(Color(0xFF1A3557),Color(0xFF2D7D5A),Color(0xFFA07000),Color(0xFF6B2FA0),Color(0xFFC0392B));return c[n.hashCode().let{if(it<0)-it else it}%c.size]}
private fun fmtTime(t:Long):String{val d=System.currentTimeMillis()-t;return when{d<86400000->SimpleDateFormat("HH:mm",Locale.getDefault()).format(Date(t));d<604800000->SimpleDateFormat("EEE",Locale.getDefault()).format(Date(t));else->SimpleDateFormat("dd MMM",Locale.getDefault()).format(Date(t))}}
