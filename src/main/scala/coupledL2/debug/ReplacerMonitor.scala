package coupledL2.debug

import chisel3._
import chisel3.util._
import coupledL2.MetaData._
import coupledL2._
import org.chipsalliance.cde.config.Parameters
import utility._

class ReplMoni(implicit p: Parameters) extends L2Bundle {
  val req_s3 = Output(new DirRead())
  val dirResult_s3 = ValidIO(new DirResult())
  val replInfo = Output(new ReplInfo())
}


class TubinsInfo(implicit p: Parameters) extends L2Bundle {
  val channel = UInt(3.W)
  val opcode = UInt(3.W)
  //val addr = UInt((tagBits + setBits).W)
  val tag = UInt(tagBits.W)
  val sset = UInt(setBits.W) //'set' is C++ common word, use 'sset' instead
  val way_s3 = UInt(wayBits.W)
  val hit = Bool()
  val TC = UInt(2.W)
  val UC = UInt(2.W)
  val Dvec = Vec(16, UInt(20.W))
  val Lvec = Vec(16, UInt(20.W))
  val DLvec = Vec(16, UInt(20.W))
  val DLcond1 = Bool()
  val DLcond2 = Bool()
  val DLcond3 = Bool()
  val isSample = Bool()
  val refill = Bool()
  val repl_state = UInt(32.W)
  val next_state = UInt(32.W)
}



class ReplacerMonitor(implicit p: Parameters) extends L2Module {
  val io = IO(new Bundle() {
    val fromDir = Input(new ReplMoni())
  })

  val dir_s3_valid = io.fromDir.dirResult_s3.valid
  val req_s3 = io.fromDir.req_s3
  val dirResult_s3 = io.fromDir.dirResult_s3.bits
  val replInfo = io.fromDir.replInfo


  /* ======== ChiselDB ======== */
  if (cacheParams.enableReplacerMonitor && !cacheParams.FPGAPlatform) {
    val hartId = cacheParams.hartId
    val table = ChiselDB.createTable(s"L2tubins", new TubinsInfo, basicDB = true)
    val tubinsInfo = Wire(new TubinsInfo())
    tubinsInfo.channel := req_s3.replacerInfo.channel
    tubinsInfo.opcode := req_s3.replacerInfo.opcode
    tubinsInfo.tag := req_s3.tag
    tubinsInfo.sset := req_s3.set
    tubinsInfo.way_s3 := dirResult_s3.way
    tubinsInfo.hit := dirResult_s3.hit
    tubinsInfo.TC := replInfo.TC
    tubinsInfo.UC := replInfo.UC
    tubinsInfo.Dvec := replInfo.Dvec
    tubinsInfo.Lvec := replInfo.Lvec
    tubinsInfo.DLvec := replInfo.DLvec
    tubinsInfo.DLcond1 := replInfo.DLcond1
    tubinsInfo.DLcond2 := replInfo.DLcond2
    tubinsInfo.DLcond3 := replInfo.DLcond3
    tubinsInfo.isSample := replInfo.isSample
    tubinsInfo.refill := replInfo.refill
    tubinsInfo.repl_state := replInfo.repl_state
    tubinsInfo.next_state := replInfo.next_state

    table.log(tubinsInfo, dir_s3_valid, s"L2${hartId}_${p(SliceIdKey)}", clock, reset)
  }
}
