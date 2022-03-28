package io.github.davidec00.cardstack.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.github.davidec00.cardstack.CardStack


class MainActivity : ComponentActivity() {
    @OptIn(androidx.compose.material.ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val item1 = Item(
            "https://f4.bcbits.com/img/0020592180_10.jpg",
            "Jane",
            "16 miles near you"
        )
        val item2 = Item(
            "https://images.pexels.com/photos/91224/pexels-photo-91224.jpeg?auto=compress&cs=tinysrgb&dpr=3&h=750&w=1260",
            "Robert",
            "7 miles near you"
        )
        val item3 = Item(
            "https://images.pexels.com/photos/789812/pexels-photo-789812.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940",
            "Daria",
            "3 miles from you"
        )
        val item4 = Item(
            "https://images.pexels.com/photos/872756/pexels-photo-872756.jpeg?cs=srgb&dl=pexels-dishan-lathiya-872756.jpg&fm=jpg",
            "Violet",
            "43 miles from you"
        )
        val items = mutableListOf(item1, item2, item3, item4)
        super.onCreate(savedInstanceState)

        setContent {
            CardStack(
                modifier = Modifier,
                enableButtons = true,
                items = items,
                rightView = { RightOverlay() },
                leftView = { LeftOverlay() },
                topView = { TopOverlay() }
            ) { item: Item ->
                Card(Modifier, item)

            }
        }
    }
}

@Composable
fun RightOverlay(
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Green)
    )

}

@Composable
fun TopOverlay(
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Blue)
    )

}

@Composable
fun LeftOverlay(
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Red)
    )

}

@Composable
fun Card(
    modifier: Modifier = Modifier,
    item: Item = Item(),
) {

    Box(
        modifier
    ) {
        if (item.url != null) {
            AsyncImage(
                model = item.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
        ) {
            Text(
                text = item.text,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier.clickable(onClick = {}) // disable the highlight of the text when dragging
            )
            Text(
                text = item.subText,
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.clickable(onClick = {}) // disable the highlight of the text when dragging
            )

        }
    }
}

data class Item(
    val url: String? = null,
    val text: String = "",
    val subText: String = ""
)



