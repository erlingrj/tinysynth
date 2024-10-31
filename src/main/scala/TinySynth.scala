package tinysynth

import chisel3._
import chisel3.util._



class TinySynth(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val pwmOut = Output(Bool())
    val uartIn = Input(Bool())
  })
  val pwm = Module(new PulseWidthModulator(cfg))
  val merge = Module(new Merge(cfg))
  val uart = Module(new UART(cfg))
  val midi = Module(new MIDI(cfg))
  uart.io.in := io.uartIn
  midi.io.uartIn <> uart.io.out
  pwm.io.dutyCycle := merge.io.wavesOut
  io.pwmOut := pwm.io.out

  for (i <- 0 until cfg.numSynths) {
    val ui = Module(new ChannelController(cfg))

    when(midi.io.midiOut.valid && midi.io.midiOut.bits.hdr.channel ===i.U) {
      ui.io.midiIn := midi.io.midiOut
    }.otherwise{
      ui.io.midiIn := 0.U.asTypeOf(ui.io.midiIn)
    }

    val osc = Module(new Oscillator(cfg))

    osc.io.period := ui.io.period
    osc.io.oscillatorSelector := ui.io.oscillatorSelector
    merge.io.wavesIn(i) := osc.io.wave


  }

}
