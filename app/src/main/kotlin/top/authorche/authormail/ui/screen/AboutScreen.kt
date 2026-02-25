package top.authorche.authormail.ui.screen
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class AL(val emoji:String,val label:String,val desc:String,val url:String,val icon:ImageVector)
private val LINKS=listOf(
    AL("🌐","Main Site","authorche.top","https://authorche.top",Icons.Outlined.Language),
    AL("🎶","Music","Musical works","https://authorche.top/music",Icons.Outlined.MusicNote),
    AL("✍️","Poems","Poetry & writing","https://authorche.top/poems",Icons.Outlined.AutoStories),
    AL("🌎","Other Links","All profiles & links","https://authorche.top/links",Icons.Outlined.Link),
    AL("💼","Dev Services","Software & web services","https://authorche.top/dev",Icons.Outlined.Code),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack:()->Unit){
    val ctx=LocalContext.current
    Scaffold(topBar={TopAppBar(title={Text("About Author")},
        navigationIcon={IconButton(onBack){Icon(Icons.Filled.ArrowBack,"Back")}},
        colors=TopAppBarDefaults.topAppBarColors(containerColor=Color.Transparent))}) { pad ->
        Column(Modifier.fillMaxSize().padding(pad).verticalScroll(rememberScrollState()),
            horizontalAlignment=Alignment.CenterHorizontally){
            // Hero
            Box(Modifier.fillMaxWidth().height(200.dp)
                .background(Brush.linearGradient(listOf(Color(0xFF0A1628),Color(0xFF1A3557),Color(0xFF1A5C3E)))),
                contentAlignment=Alignment.Center){
                Column(horizontalAlignment=Alignment.CenterHorizontally){
                    Box(Modifier.size(80.dp).clip(CircleShape)
                        .background(Brush.radialGradient(listOf(Color(0xFF2D7D5A),Color(0xFF1A3557)))),
                        contentAlignment=Alignment.Center){Text("✍️",fontSize=34.sp)}
                    Spacer(Modifier.height(10.dp))
                    Text("Vadim Yemelianov",fontWeight=FontWeight.Bold,fontSize=20.sp,color=Color.White)
                    Text("AuthorChe",color=Color(0xFF5DDC9A),fontSize=14.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Developer · Author · Musician",color=MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(20.dp))
            Column(Modifier.padding(horizontal=16.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
                Text("My Spaces",style=MaterialTheme.typography.titleMedium,color=MaterialTheme.colorScheme.primary,modifier=Modifier.padding(start=4.dp))
                LINKS.forEach { l ->
                    Card(onClick={ctx.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse(l.url)))},
                        Modifier.fillMaxWidth(),shape=RoundedCornerShape(14.dp),
                        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceVariant)){
                        Row(Modifier.padding(16.dp,14.dp),verticalAlignment=Alignment.CenterVertically){
                            Box(Modifier.size(44.dp).clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment=Alignment.Center){Text(l.emoji,fontSize=20.sp)}
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)){
                                Text(l.label,style=MaterialTheme.typography.titleSmall,fontWeight=FontWeight.SemiBold)
                                Text(l.desc,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Outlined.OpenInNew,null,tint=MaterialTheme.colorScheme.primary,modifier=Modifier.size(18.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            ElevatedCard(Modifier.padding(horizontal=16.dp).fillMaxWidth(),shape=RoundedCornerShape(16.dp)){
                Column(Modifier.padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(4.dp)){
                    Text("AuthorMail",style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.Bold)
                    Text("v1.0.0",style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.outline)
                    Spacer(Modifier.height(4.dp))
                    Text("Kotlin · Jetpack Compose · Material Design 3\nGemini AI Spam Protection",
                        style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant,textAlign=TextAlign.Center)
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
