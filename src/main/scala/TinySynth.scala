package tinysynth

import chisel3._
import chisel3.util._


class Merge(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val wavesIn= Vec(cfg.numSynths, Input(UInt(cfg.pwmOutputBits.W)))
    val wavesOut= Output(UInt(cfg.pwmOutputBits.W))
  })

  io.wavesOut := io.wavesIn.reduce(_ + _)
}

class TinySynth(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val pwmOut = Output(Bool())
    val adcIn = Vec(cfg.numSynths, Input(Bool()))
    val adcOut = Vec(cfg.numSynths, Output(Bool()))
  })
  val pwm = Module(new PulseWidthModulator(cfg))
  val merge = Module(new Merge(cfg))
  pwm.io.dutyCycle := merge.io.wavesOut
  io.pwmOut := pwm.io.out

  for (i <- 0 until cfg.numSynths) {
    val ui = Module(new ChannelController(cfg))
    val osc = Module(new Oscillator(cfg))
    val adc = Module(new InputADC(cfg))
    adc.io.analogIn := io.adcIn(i)
    io.adcOut(i) := adc.io.analogOut

    ui.io.freqKnob := adc.io.digitalOut

    osc.io.period := ui.io.period
    osc.io.oscillatorSelector := ui.io.oscillatorSelector
    merge.io.wavesIn(i) := osc.io.wave
  }

}
