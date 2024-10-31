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
