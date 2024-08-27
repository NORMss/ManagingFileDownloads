package com.norm.mymanagingfiledownloads

import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ketch.Ketch
import com.ketch.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

const val FILE_TAG = "video"
const val FILE_NAME = "test_video_ketchlib.mp4"
const val DOWNLOAD_URL =
    "https://rr3---sn-4g5ednds.googlevideo.com/videoplayback?expire=1724799020&ei=zAPOZpbDDJu4rtoP65vlaQ&ip=103.62.147.130&id=o-AKfYxeMWENAqaOYP1VbVZ2-FcWpUSN_HXf_fzJjVWGfb&itag=18&source=youtube&requiressl=yes&xpc=EgVo2aDSNQ%3D%3D&bui=AQmm2eyyHFDNZPdclOE6y_kD2XmggskS1HUjgIAoMiK5-PK3MvfY6bfRxC7jA8ed4jQMjwU8ArU3PYpa&spc=Mv1m9jc9vnFfiHP8xRkwgpnGi3KzT_GAWZ2S53HdWgg7VpdfatnWn1vCo-CAGo4&vprv=1&svpuc=1&mime=video%2Fmp4&ns=ycJ76ObKGwCpi43nL_reH-4Q&rqh=1&gir=yes&clen=11668926&ratebypass=yes&dur=430.915&lmt=1724766174655806&c=WEB&sefc=1&txp=5319224&n=tvTQn4gPbNRWiA&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cxpc%2Cbui%2Cspc%2Cvprv%2Csvpuc%2Cmime%2Cns%2Crqh%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AJfQdSswRAIgTM26LDMn3hM40BWgyzr1uVI2Z8Eh1BUUg1D_SBm5Mm4CIHoBlgKbNu008zrYhjtu4k7EN_LTau6cy9Meou2qovnW&title=All%20in%20One%20Solution%20for%20Managing%20File%20Downloads%20in%20Android%20-%20Ketch&rm=sn-2gxjpv0noxujvh-qxal7e,sn-qxaez76&rrc=79,104,80&fexp=24350516,24350517,24350557,24350561&req_id=1348e912da52a3ee&ipbypass=yes&redirect_counter=3&cm2rm=sn-n8vdre7&cms_redirect=yes&cmsv=e&mh=6z&mip=5.44.168.51&mm=34&mn=sn-4g5ednds&ms=ltu&mt=1724777077&mv=m&mvi=3&pl=26&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AGtxev0wRQIgHOdmpcjkfcgl6UhSrO1wuGpm2EkM-67m8ktW24TBJc4CIQCe6duz_KdeUBYYrjEGjh1AoxZHwD-_sD1-_buTzpOgyQ%3D%3D"


@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    ketch: Ketch,
) {
    val scope = rememberCoroutineScope()

    var states by remember {
        mutableStateOf(Status.DEFAULT)
    }
    var progrss by remember {
        mutableIntStateOf(0)
    }
    var total by remember {
        mutableLongStateOf(0L)
    }
    var isCollecting by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isCollecting) {
        if (isCollecting) {
            ketch.observeDownloadByTag(
                tag = FILE_TAG
            )
                .collect { downloadModel ->
                    downloadModel.forEach { model ->
                        states = model.status
                        progrss = model.progress
                        total = model.total
                    }
                }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = states.name,
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Medium,
        )
        Spacer(
            modifier = Modifier.height(6.dp)
        )
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = {
                progrss / 100f
            }
        )
        Spacer(
            modifier = Modifier.height(6.dp)
        )
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = "${
                calculateDownloadedMegabytes(
                    progrss,
                    total,
                )
            } MB / ${getTwoDecimals(value = total / (1024.0 * 1024.0))} MB"
        )
        Spacer(
            modifier = Modifier.height(6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = {
                    isCollecting = true
                    ketch.download(
                        tag = FILE_TAG,
                        url = DOWNLOAD_URL,
                        fileName = FILE_NAME,
                        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                    )
                }
            ) {
                Text(
                    text = "Download"
                )
            }
            Spacer(
                modifier = Modifier.width(8.dp)
            )
            Button(
                onClick = {
                    ketch.cancel(FILE_TAG)
                }
            ) {
                Text(
                    text = "Cancel"
                )
            }
            Spacer(
                modifier = Modifier
                    .width(8.dp)
            )
        }
        Spacer(
            modifier = Modifier.height(6.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = {
                    ketch.pause(tag = FILE_TAG)
                }
            ) {
                Text(
                    text = "Pause"
                )
            }
            Button(
                onClick = {
                    ketch.resume(tag = FILE_TAG)
                }
            ) {
                Text(
                    text = "Resume"
                )
            }
            Button(
                onClick = {
                    ketch.retry(tag = FILE_TAG)
                }
            ) {
                Text(
                    text = "Retry"
                )
            }
        }
        Spacer(
            modifier = Modifier
                .height(8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = {
                    ketch.clearDb(tag = FILE_TAG)
                    scope.launch {
                        delay(100)
                        states = Status.DEFAULT
                        progrss = 0
                        total = 0L
                        isCollecting = false
                    }
                }
            ) {
                Text(
                    text = "Delete"
                )
            }
        }
    }
}

private fun calculateDownloadedMegabytes(progress: Int, totalBytes: Long): String {
    val downloadedBytes = progress / 100.0 * totalBytes
    return getTwoDecimals(value = downloadedBytes / (1024.0 * 1024.0))
}

private fun getTwoDecimals(value: Double): String {
    return String.format(Locale.ROOT, "%.2f", value)
}