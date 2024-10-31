package tinysynth

import chisel3._
import chisel3.util._

class MidiHdr extends Bundle {
  val channel = UInt(4.W)
  val msgType = UInt(4.W)
}

class MidiNoteOnOff extends Bundle {
  val hdr = new MidiHdr()
  val note = UInt(8.W)
  val velocity = UInt(8.W)
}

object MIDI {
  val NOTE_OFF = 8.U(4.W)
  val NOTE_ON = 9.U(4.W)
}

class MIDI(cfg: TinySynthConfig) extends Module {
  val io = IO(new Bundle {
    val uartIn = Flipped(Decoupled(UInt(8.W)))
    val midiOut = Valid(new MidiNoteOnOff())
  })


  val sReadHdr :: sReadData1 :: sReadData2 :: sOutput :: Nil = Enum(4)
  val regState = RegInit(sReadHdr)
  val regOutput = RegInit(0.U.asTypeOf(new MidiNoteOnOff()))

  io.midiOut.bits := regOutput
  io.midiOut.valid := false.B // Default false except in sOutput
  io.uartIn.ready := false.B // Default to false.
  switch(regState) {
    is (sReadHdr) {
      io.uartIn.ready := true.B
      when(io.uartIn.fire) {
        val hdr = io.uartIn.bits.asTypeOf(new MidiHdr())
        when(hdr.msgType === MIDI.NOTE_ON || hdr.msgType === MIDI.NOTE_OFF) {
          regOutput.hdr := io.uartIn.bits.asTypeOf(new MidiHdr())
          regState := sReadData1
        }.elsewhen(hdr.msgType(3) === 0.U) {
          // This means that we are actually receiving a chain of MIDI messages
          // so treat it as sReadData1
          regOutput.note := io.uartIn.bits
          regState := sReadData2
        }
      }
    }
    is (sReadData1) {
      io.uartIn.ready := true.B
      when (io.uartIn.fire) {
        regOutput.note := io.uartIn.bits
        regState := sReadData2
      }
    }
    is (sReadData2) {
      io.uartIn.ready := true.B
      when (io.uartIn.fire) {
        regOutput.velocity := io.uartIn.bits
        regState := sOutput
      }
    }
    is (sOutput) {
      io.midiOut.valid := true.B
      regState := sReadHdr
    }
  }
}
