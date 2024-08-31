package ufc.smd.esqueleto_placar

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.getSystemService
import data.Placar
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets
import kotlin.math.log

class PlacarActivity : AppCompatActivity() {
    lateinit var placar:Placar
    lateinit var tvResultadoJogo: TextView
    lateinit var tvCartasVermelho: TextView
    lateinit var tvCartasColocadas: TextView

    var game =0
    var cartasAzul: Int = 5
    var cartasVermelho: Int = 5
    var cartasColocadas: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_placar)
        placar = getIntent().getExtras()?.getSerializable("placar") as Placar
        tvResultadoJogo= findViewById(R.id.tvPlacar)
        tvCartasVermelho= findViewById(R.id.tvPlacar2)
        tvCartasColocadas= findViewById(R.id.tvCartas)

        //Mudar o nome da partida
        val tvNomePartida=findViewById(R.id.tvNomePartida2) as TextView
        tvNomePartida.text=placar.nome_partida
        ultimoJogos()
    }

    fun alteraAzul (v:View){
        if (cartasAzul < 9){
            cartasAzul++
            cartasVermelho--
            vibrar(v)
        }
        //placar.resultado = "" + cartasAzul + " vs " + cartasVermelho
        alteraPlacar(cartasAzul, cartasVermelho)

    }

    fun alteraVermelho (v:View){
        if (cartasVermelho < 9){
            cartasVermelho++
            cartasAzul--
            vibrar(v)
        }

        //placar.resultado = "" + cartasAzul + " vs " + cartasVermelho

        alteraPlacar(cartasAzul, cartasVermelho)
        /*
        if ((game % 2) != 0) {
            placar.resultado = ""+game+" vs "+ (game-1)
        }else{
            placar.resultado = ""+(game-1)+" vs "+ (game-1)
            vibrar(v)
        }
        */


    }

    fun alteraCartas(v:View){
        if(cartasColocadas < 9){
            cartasColocadas++
        }
        tvCartasColocadas.text = cartasColocadas.toString()

    }

    fun alteraPlacar(val1: Int, val2: Int){
        cartasAzul = val1
        cartasVermelho = val2
        placar.resultado = "" + cartasAzul + " vs " + cartasVermelho
        tvResultadoJogo.text= cartasAzul.toString()
        tvCartasVermelho.text = cartasVermelho.toString()
    }


    fun vibrar (v:View){
        val buzzer = this.getSystemService<Vibrator>()
         val pattern = longArrayOf(0, 200, 100, 300)
         buzzer?.let {
             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 buzzer.vibrate(VibrationEffect.createWaveform(pattern, -1))
             } else {
                 //deprecated in API 26
                 buzzer.vibrate(pattern, -1)
             }
         }

    }


    fun saveGame(v: View) {
        alteraPlacar(cartasAzul, cartasVermelho)

        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)
        var edShared = sp.edit()
        //Salvar o número de jogos já armazenados
        var numMatches= sp.getInt("numberMatch",0) + 1
        edShared.putInt("numberMatch", numMatches)

        //Escrita em Bytes de Um objeto Serializável
        var dt= ByteArrayOutputStream()
        var oos = ObjectOutputStream(dt);
        oos.writeObject(placar);

        //Salvar como "match"
        edShared.putString("match"+numMatches, dt.toString(StandardCharsets.ISO_8859_1.name()))
        edShared.commit() //Não esqueçam de comitar!!!

    }

    fun lerUltimosJogos(v: View){
        val sharedFilename = "PreviousGames"
        val sp: SharedPreferences = getSharedPreferences(sharedFilename, Context.MODE_PRIVATE)
        Log.v("PDM","number match: " + sp.getInt("numberMatch",0).toString())
        val matchNumber: Int = sp.getInt("numberMatch",0)
        var meuObjString:String= sp.getString("match"+ matchNumber.toString(),"").toString()
        Log.v("PDM", "Match: " + meuObjString)
        if (meuObjString.length >=1) {
            var dis = ByteArrayInputStream(meuObjString.toByteArray(Charsets.ISO_8859_1))
            var oos = ObjectInputStream(dis)
            var placarAntigo:Placar=oos.readObject() as Placar
            Log.v("PDM", "placarAntigo: " + placarAntigo)
            //                                       primeiro valor                         segundo valor
            Log.v("PDM", "placar Antigo: " + placarAntigo.resultado[0] + " vs " + placarAntigo.resultado[5])
            //placar.resultado = placarAntigo.resultado
            //                    Valor do azul                         Valor do vermelho
            alteraPlacar(placarAntigo.resultado[0].digitToInt(), placarAntigo.resultado[5].digitToInt())
            Log.v("SMD26",placar.resultado)


        }
    }




    fun ultimoJogos () {
        val sharedFilename = "PreviousGames"
        val sp:SharedPreferences = getSharedPreferences(sharedFilename,Context.MODE_PRIVATE)
        var matchStr:String=sp.getString("match1","").toString()
       // Log.v("PDM22", matchStr)
        if (matchStr.length >=1){
            var dis = ByteArrayInputStream(matchStr.toByteArray(Charsets.ISO_8859_1))
            var oos = ObjectInputStream(dis)
            var prevPlacar:Placar = oos.readObject() as Placar
            Log.v("PDM22", "Jogo Salvo:"+ prevPlacar.resultado)
        }

    }
}