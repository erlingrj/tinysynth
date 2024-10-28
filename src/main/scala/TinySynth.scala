package tinysynth

import chisel3._
import chisel3.util._


class TinySynth(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val pwmOut = Vec(cfg.numSynths, Bool())
  })

  for (i <- 0 until cfg.numSynths) {
    val ui = Module(new ChannelController(cfg))
    val osc = Module(new Oscillator(cfg))
    val pwm = Module(new PulseWidthModulator(cfg))

    osc.io.period := ui.io.period
    osc.io.oscillatorSelector := ui.io.oscillatorSelector
    io.pwmOut(i) := pwm.io.out
  }



}
