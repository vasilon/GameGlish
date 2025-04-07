import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.animation.with

@OptIn(ExperimentalAnimationApi::class)
@Suppress("DEPRECATION")
@Composable
fun AnimatedScreen(content: @Composable () -> Unit) {
    AnimatedContent(targetState = Unit, transitionSpec = {
        fadeIn(animationSpec = tween(300)) with fadeOut(animationSpec = tween(300))
    }) {
        content()
    }
}