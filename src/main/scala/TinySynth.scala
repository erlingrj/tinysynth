package tinysynth

import chisel3._
import chisel3.util._


class TinySynth(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val pwmOut = Vec(cfg.numSynths, Output(Bool()))
    val adcIn = Vec(cfg.numSynths, Input(Bool()))
    val adcOut = Vec(cfg.numSynths, Output(Bool()))
  })

  for (i <- 0 until cfg.numSynths) {
    val ui = Module(new ChannelController(cfg))
    val osc = Module(new Oscillator(cfg))
    val pwm = Module(new PulseWidthModulator(cfg))
    val adc = Module(new InputADC(cfg))
    adc.io.analogIn := io.adcIn(i)
    io.adcOut(i) := adc.io.analogOut

    ui.io.freqKnob := adc.io.digitalOut

    osc.io.period := ui.io.period
    osc.io.oscillatorSelector := ui.io.oscillatorSelector

    pwm.io.dutyCycle := osc.io.wave// TODO: I cannot just connect directly here I think...
    io.pwmOut(i) := pwm.io.out
  }



}
