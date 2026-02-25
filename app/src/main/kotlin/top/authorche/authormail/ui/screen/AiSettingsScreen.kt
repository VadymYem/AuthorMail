package top.authorche.authormail.ui.screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import top.authorche.authormail.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSettingsScreen(vm: MainViewModel = hiltViewModel(), onBack: () -> Unit) {
    val state by vm.state.collectAsState()
    val s = state.settings
    var key by remember(s.geminiApiKey) { mutableStateOf(s.geminiApiKey) }
    var showKey by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var wlInput by remember { mutableStateOf("") }

    Scaffold(topBar={ TopAppBar(title={Text("AI & Privacy Settings")},
        navigationIcon={IconButton(onBack){Icon(Icons.Filled.ArrowBack,"Back")}}) }) { pad ->
        LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding=PaddingValues(16.dp),
            verticalArrangement=Arrangement.spacedBy(16.dp)) {

            // ── Gemini Key ──────────────────────────────────────────────────
            item {
                Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    Icon(Icons.Filled.SmartToy,null,tint=MaterialTheme.colorScheme.primary)
                    Text("Gemini AI",style=MaterialTheme.typography.titleMedium,color=MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(8.dp))
                // ── Попередження про передачу даних ───────────────────────
                Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.tertiaryContainer)) {
                    Row(Modifier.padding(12.dp), horizontalArrangement=Arrangement.spacedBy(8.dp)){
                        Icon(Icons.Filled.Info,null,tint=MaterialTheme.colorScheme.onTertiaryContainer,modifier=Modifier.size(18.dp))
                        Text("AI spam filtering requires sending email metadata (sender, subject, snippet) to Google's Gemini API. Do not enable if your emails contain confidential data.",
                            style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onTertiaryContainer)
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(key,{key=it},Modifier.fillMaxWidth(),label={Text("Your Gemini API Key")},
                    placeholder={Text("AIza...")},singleLine=true,
                    visualTransformation=if(showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),
                    trailingIcon={ Row{
                        IconButton({showKey=!showKey}){Icon(if(showKey)Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,"Toggle")}
                        IconButton({vm.saveKey(key)}){Icon(Icons.Filled.Save,"Save")}
                    }},
                    supportingText={Text("Get free key at aistudio.google.com → your key stays on device, never sent to us",
                        color=MaterialTheme.colorScheme.outline,style=MaterialTheme.typography.labelSmall)})
            }

            // ── AI Toggle ───────────────────────────────────────────────────
            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically){
                            Column(Modifier.weight(1f)){
                                Text("AI Spam Detection",style=MaterialTheme.typography.titleMedium)
                                Text("Analyse incoming emails via Gemini before displaying",
                                    style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(s.aiSpamEnabled,{vm.setAiOn(it)})
                        }
                        if(s.aiSpamEnabled){
                            HorizontalDivider(Modifier.padding(vertical=12.dp))
                            Text("Spam threshold: ${(s.spamThreshold*100).toInt()}%",style=MaterialTheme.typography.labelMedium)
                            Slider(s.spamThreshold,{vm.setThresh(it)},valueRange=0.5f..0.99f,steps=9)
                            Text("Emails with AI confidence above this are flagged",
                                style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.outline)
                            HorizontalDivider(Modifier.padding(vertical=12.dp))
                            Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically){
                                Column(Modifier.weight(1f)){
                                    Text("Auto-Block → Trash",style=MaterialTheme.typography.titleSmall)
                                    Text("Spam is moved to Trash automatically without review",
                                        style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.error.copy(alpha=0.8f))
                                }
                                Switch(s.aiAutoBlocking,{ if(it) showBlockDialog=true else vm.setBlock(false)})
                            }
                        }
                    }
                }
            }

            // ── Whitelist ───────────────────────────────────────────────────
            item {
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    Icon(Icons.Filled.VerifiedUser,null,tint=MaterialTheme.colorScheme.primary)
                    Text("Trusted Senders — Whitelist",style=MaterialTheme.typography.titleMedium,color=MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(4.dp))
                Text("Whitelisted addresses skip AI analysis entirely and always reach Inbox.",
                    style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(8.dp)){
                    OutlinedTextField(wlInput,{wlInput=it},Modifier.weight(1f),
                        label={Text("Add trusted address")},singleLine=true,
                        keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email))
                    FilledTonalIconButton({if(wlInput.isNotBlank()){vm.addWl(wlInput.trim());wlInput=""}}){
                        Icon(Icons.Filled.Add,"Add")
                    }
                }
            }
            items(state.whitelist.toList().sorted()) { addr ->
                ElevatedCard(Modifier.fillMaxWidth()){
                    Row(Modifier.padding(16.dp,10.dp),verticalAlignment=Alignment.CenterVertically){
                        Icon(Icons.Filled.CheckCircle,null,tint=MaterialTheme.colorScheme.secondary,modifier=Modifier.size(18.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(addr,Modifier.weight(1f))
                        IconButton({vm.removeWl(addr)},Modifier.size(32.dp)){
                            Icon(Icons.Filled.Close,"Remove",modifier=Modifier.size(16.dp),tint=MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }

    // ── Auto-Block Disclaimer Dialog (MD3) ────────────────────────────────────
    if(showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog=false },
            icon = { Icon(Icons.Filled.WarningAmber,null,tint=MaterialTheme.colorScheme.error) },
            title = { Text("⚠ Enable Auto-Block?") },
            text = {
                Column(verticalArrangement=Arrangement.spacedBy(8.dp)){
                    Text("Please read carefully before enabling:",style=MaterialTheme.typography.titleSmall)
                    HorizontalDivider()
                    Text("1. False Positives — legitimate emails may be incorrectly classified as spam and moved to Trash. You may miss important messages permanently.")
                    Text("2. Data Privacy — sender address, subject and a snippet of the email body are sent to Google Gemini API for analysis. Do not use with confidential communications.")
                    Text("3. No Guarantees — AI analysis is probabilistic. The authors of AuthorMail bear no responsibility for emails lost due to this feature.")
                    Text("4. Your API Key — costs for Gemini API calls are your responsibility.")
                    HorizontalDivider()
                    Text("Tip: add trusted senders to the Whitelist to exclude them from AI analysis.",
                        style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.secondary)
                }
            },
            confirmButton = {
                Button({vm.setBlock(true);showBlockDialog=false},
                    colors=ButtonDefaults.buttonColors(containerColor=MaterialTheme.colorScheme.error)){
                    Text("I Understand, Enable")
                }
            },
            dismissButton = {
                OutlinedButton({showBlockDialog=false}){ Text("Cancel") }
            }
        )
    }
}
