package com.gabriel_miranda.currencyapp.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.gabriel_miranda.currencyapp.domain.model.Currency
import com.gabriel_miranda.currencyapp.domain.model.CurrencyCode
import com.gabriel_miranda.currencyapp.domain.model.CurrencyType
import com.gabriel_miranda.currencyapp.domain.model.RequestState
import currencyapp.composeapp.generated.resources.Res
import currencyapp.composeapp.generated.resources.switch_ic
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CurrencyInputs(
    source: RequestState<Currency>,
    target: RequestState<Currency>,
    onSwitchClick: () -> Unit,
    onCurrencyTypeSelect: (CurrencyType) -> Unit
) {

    var animationStarted by remember { mutableStateOf(false) }
    val animatedRotation by animateFloatAsState(
        targetValue = if (animationStarted) 180f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CurrencyView(
            placeholder = "from",
            currency = source,
            onClick = {
                if (source.isSuccess()) {
                    onCurrencyTypeSelect(
                        CurrencyType.Source(
                            currencyCode = CurrencyCode.valueOf(
                                source.getSuccessData().code
                            )
                        )
                    )
                }
            }
        )
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(
            modifier = Modifier
                .padding(top = 24.dp)
                .graphicsLayer {
                    rotationY = animatedRotation
                },
            onClick = {
                animationStarted = !animationStarted
                onSwitchClick()
            }
        ) {
            Icon(
                painter = painterResource(Res.drawable.switch_ic),
                contentDescription = "Switch Icon",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        CurrencyView(
            placeholder = "to",
            currency = target,
            onClick = {
                if (target.isSuccess()) {
                    onCurrencyTypeSelect(
                        CurrencyType.Target(
                            currencyCode = CurrencyCode.valueOf(
                                target.getSuccessData().code
                            )
                        )
                    )
                }
            }
        )
    }
}