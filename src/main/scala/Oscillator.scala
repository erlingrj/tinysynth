package tinysynth

import chisel3._
import chisel3.util._

object OscillatorType extends ChiselEnum {
  val SquareWave, SineWave, Sawtooth = Value
}

class Oscillator(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val wave = Output(UInt(cfg.pwmOutputBits.W))
    val oscillatorSelector = Input(OscillatorType())
    val period = Input(UInt(cfg.oscPeriodBits.W))
  })

  val square = if (cfg.hasSquareWaveOscillator) {
    val m = Module(new SquareWaveOscillator(cfg))
    m.io.period := io.period
    Some(m)
  } else {
    None
  }
  io.wave := square.get.io.wave
}

class SquareWaveOscillator(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val wave = Output(UInt(cfg.pwmOutputBits.W))
    val period = Input(UInt(cfg.oscPeriodBits.W))
  })

  val cnt = RegInit(0.U(cfg.oscPeriodBits.W))
  val halfPeriod = (io.period >> 1).asUInt
  io.wave := cnt < halfPeriod

  cnt := cnt + 1.U
  when (cnt === (io.period - 1.U)) {
    cnt := 0.U
  }
}