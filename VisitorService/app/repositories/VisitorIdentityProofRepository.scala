package repositories

import models.VisitorIdentityProofTable
import play.api.db.slick.DatabaseConfigProvider
import requests.VisitorIdentityProof
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}



class VisitorIdentityProofRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._


  private val visitorsIdentity = TableQuery[VisitorIdentityProofTable]

  def create(visitorIdentity: VisitorIdentityProof): Future[Int] = {
    val insertQueryThenReturnId = visitorsIdentity
      .map(v => (v.visitorId, v.identityProof))
      .returning(visitorsIdentity.map(_.id))  // Ensure this returns a Int value

    /** Execute the query and return the inserted visitor's ID */
    db.run(insertQueryThenReturnId += (
      visitorIdentity.visitorId,
      visitorIdentity.identityProof
    )).map(_.head)  // Extract the first element from the result (the ID)
  }

  def list(): Future[Seq[VisitorIdentityProof]] = db.run(visitorsIdentity.result)

  def getById(id: Int): Future[Option[VisitorIdentityProof]] = db.run(visitorsIdentity.filter(_.visitorId === id).result.headOption)
}

