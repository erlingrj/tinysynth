package tinysynth
import chisel3._

class ChiselTop() extends Module {
  val io = IO(new Bundle {
    val ui_in = Input(UInt(8.W))      // Dedicated inputs
    val uo_out = Output(UInt(8.W))    // Dedicated outputs
    val uio_in = Input(UInt(8.W))     // IOs: Input path
    val uio_out = Output(UInt(8.W))   // IOs: Output path
    val uio_oe = Output(UInt(8.W))    // IOs: Enable path (active high: 0=input, 1=output)
    val ena = Input(Bool())           // will go high when the design is enabled
  })

  val cfg = DefaultConfig
  val tinySynth = Module(new TinySynth(DefaultConfig))
  // Forward TinySynth outputs
  io.uo_out := VecInit(Seq(tinySynth.io.pwmOut) ++ tinySynth.io.adcOut).asUInt

  // Forward to TinySynths inputs
  for ((tinyIO, i) <- tinySynth.io.adcIn.zipWithIndex) {
    tinyIO := io.ui_in(i)
  }

  // Drive currently unused signals to 0
  io.uio_out := 0.U
  io.uio_oe := 0.U

  // FIXME: What about the enable signal? Should it control the reset?
}

object ChiselTop extends App {
  emitVerilog(new ChiselTop(), Array("--target-dir", "src"))
}