package tinysynth

import chisel3._
import chisel3.util._



class ChannelController(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val freqKnob = Input(UInt(cfg.uiKnobInputBits.W))
    val volumeKnob = Input(UInt(cfg.uiKnobInputBits.W))
    val oscillatorSelector = Output(OscillatorType())
    val period = Output(UInt(cfg.oscPeriodBits.W))
    val volume = Output(UInt(cfg.volumeBits.W))
  })

  def freqToPeriod(freq: Int) = {
    val periodNs = 1000000000/freq
    val periodCC = periodNs/cfg.clockPeriodNs
    periodCC
  }

  io.oscillatorSelector := OscillatorType.SquareWave
  io.period := freqToPeriod(440).U // Just output an A for now
  io.volume := 2^(cfg.volumeBits-1).U // Set to half volume
}