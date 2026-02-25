package top.authorche.authormail.ui.screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.authorche.authormail.domain.model.OAuthProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSetupScreen(onBack: () -> Unit, onDone: () -> Unit) {
    var tab by remember { mutableIntStateOf(0) } // 0=OAuth, 1=Manual
    var oauthProvider by remember { mutableStateOf(OAuthProvider.NONE) }
    var manualEmail by remember { mutableStateOf("") }
    var manualPass by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    var imapHost by remember { mutableStateOf("") }
    var smtpHost by remember { mutableStateOf("") }
    var showOAuthInfo by remember { mutableStateOf(false) }

    Scaffold(topBar={
        TopAppBar(title={Text("Add Email Account")},
            navigationIcon={IconButton(onBack){Icon(Icons.Filled.ArrowBack,"Back")}})
    }) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement=Arrangement.spacedBy(16.dp)) {

            // Tab selector
            TabRow(tab) {
                Tab(tab==0,{tab=0},text={Text("OAuth (Recommended)")})
                Tab(tab==1,{tab=1},text={Text("Manual / App Password")})
            }

            when(tab) {
                // ── OAuth tab ───────────────────────────────────────────────
                0 -> {
                    Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.secondaryContainer)){
                        Row(Modifier.padding(12.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                            Icon(Icons.Filled.Security,null,tint=MaterialTheme.colorScheme.onSecondaryContainer,modifier=Modifier.size(20.dp))
                            Text("OAuth works like Thunderbird — you log in via your provider's official website. AuthorMail never sees your password.",
                                style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }

                    Text("Choose your provider:",style=MaterialTheme.typography.titleMedium)

                    OAuthProviderButton(
                        emoji="🔴", name="Gmail / Google Workspace",
                        description="Authorise via accounts.google.com",
                        selected=oauthProvider==OAuthProvider.GMAIL,
                        onClick={ oauthProvider=OAuthProvider.GMAIL }
                    )
                    OAuthProviderButton(
                        emoji="🔵", name="Outlook / Hotmail / Live",
                        description="Authorise via login.microsoftonline.com",
                        selected=oauthProvider==OAuthProvider.OUTLOOK,
                        onClick={ oauthProvider=OAuthProvider.OUTLOOK }
                    )

                    if(oauthProvider != OAuthProvider.NONE) {
                        // Info about needing client credentials
                        OutlinedCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(14.dp), verticalArrangement=Arrangement.spacedBy(6.dp)){
                                Row(verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(6.dp)){
                                    Icon(Icons.Filled.Info,null,Modifier.size(16.dp),tint=MaterialTheme.colorScheme.primary)
                                    Text("Setup required",style=MaterialTheme.typography.titleSmall,fontWeight=FontWeight.SemiBold)
                                }
                                Text("To use OAuth you need to register AuthorMail in the developer console:",
                                    style=MaterialTheme.typography.bodySmall)
                                when(oauthProvider){
                                    OAuthProvider.GMAIL ->
                                        Text("1. Go to console.cloud.google.com\n2. Create project → Enable Gmail API\n3. OAuth 2.0 Credentials → Android app\n4. Add redirect: top.authorche.authormail://oauth2callback\n5. Paste Client ID into OAuthManager.kt",
                                            style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                                    OAuthProvider.OUTLOOK ->
                                        Text("1. Go to portal.azure.com → App registrations\n2. Add redirect URI (mobile/desktop)\n3. Add IMAP.AccessAsUser.All permission\n4. Paste Client ID into OAuthManager.kt",
                                            style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                                    else -> {}
                                }
                                Button({/* OAuthManager.openAuthBrowser(oauthProvider) */},Modifier.fillMaxWidth()){
                                    Icon(Icons.Filled.OpenInBrowser,null,Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Open ${if(oauthProvider==OAuthProvider.GMAIL) "Google" else "Microsoft"} Login")
                                }
                            }
                        }
                    }
                }

                // ── Manual tab ──────────────────────────────────────────────
                1 -> {
                    Card(colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.tertiaryContainer)){
                        Row(Modifier.padding(12.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){
                            Icon(Icons.Filled.Warning,null,tint=MaterialTheme.colorScheme.onTertiaryContainer,modifier=Modifier.size(20.dp))
                            Text("For Gmail use an App Password (Google Account → Security → App Passwords), not your main password.",
                                style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onTertiaryContainer)
                        }
                    }
                    OutlinedTextField(manualEmail,{manualEmail=it},Modifier.fillMaxWidth(),
                        label={Text("Email address")},singleLine=true,
                        keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Email),
                        leadingIcon={Icon(Icons.Filled.Email,null)})
                    OutlinedTextField(manualPass,{manualPass=it},Modifier.fillMaxWidth(),
                        label={Text("Password / App Password")},singleLine=true,
                        visualTransformation=if(showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Password),
                        leadingIcon={Icon(Icons.Filled.Lock,null)},
                        trailingIcon={IconButton({showPass=!showPass}){Icon(if(showPass)Icons.Filled.VisibilityOff else Icons.Filled.Visibility,"Toggle")}})
                    OutlinedTextField(imapHost,{imapHost=it},Modifier.fillMaxWidth(),
                        label={Text("IMAP Host (e.g. imap.gmail.com)")},singleLine=true,
                        leadingIcon={Icon(Icons.Filled.Dns,null)})
                    OutlinedTextField(smtpHost,{smtpHost=it},Modifier.fillMaxWidth(),
                        label={Text("SMTP Host (e.g. smtp.gmail.com)")},singleLine=true,
                        leadingIcon={Icon(Icons.Filled.Send,null)})

                    Button({onDone()},Modifier.fillMaxWidth()){
                        Icon(Icons.Filled.CheckCircle,null,Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Connect Account")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OAuthProviderButton(emoji:String,name:String,description:String,selected:Boolean,onClick:()->Unit){
    Card(onClick=onClick,Modifier.fillMaxWidth(),
        shape=RoundedCornerShape(12.dp),
        colors=CardDefaults.cardColors(containerColor=if(selected)
            MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),
        border=if(selected) CardDefaults.outlinedCardBorder() else null){
        Row(Modifier.padding(16.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.spacedBy(14.dp)){
            Text(emoji,fontSize=28.sp)
            Column(Modifier.weight(1f)){
                Text(name,style=MaterialTheme.typography.titleSmall,fontWeight=FontWeight.SemiBold)
                Text(description,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if(selected) Icon(Icons.Filled.CheckCircle,null,tint=MaterialTheme.colorScheme.primary)
        }
    }
}
