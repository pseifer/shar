package de.pseifer.shar.reasoning

import org.semanticweb.HermiT.Configuration
import org.semanticweb.HermiT.Configuration.TableauMonitorType

import org.semanticweb.HermiT.Configuration.ExistentialStrategyType
import org.semanticweb.HermiT.Configuration.BlockingStrategyType
import org.semanticweb.HermiT.Configuration.DirectBlockingType
import org.semanticweb.HermiT.Configuration.BlockingSignatureCacheType

// Wrapper around HermiT Configuration.
// For documentation, see:
// https://github.com/phillord/hermit-reasoner/blob/master/src/main/java/org/semanticweb/HermiT/Configuration.java

enum HermitDirectBlocking:
  case Single
  case PairWise
  case Optimal
  case Default

enum HermitBlockingStrategy:
  case Anywhere
  case Ancestor
  case ComplexCore
  case SimpleCore
  case Optimal
  case Default

enum HermitSignatureCache:
  case Cached
  case NotCached
  case Default

enum HermitExistentialStrategy:
  case CreationOrder
  case IndividualReuse
  case El
  case Default

/** Configuration for HermiT. */
case class HermitConfiguration(
    directBlocking: HermitDirectBlocking = HermitDirectBlocking.Default,
    blockingStrategy: HermitBlockingStrategy = HermitBlockingStrategy.Default,
    signatureCache: HermitSignatureCache = HermitSignatureCache.Default,
    existentialStrategy: HermitExistentialStrategy =
      HermitExistentialStrategy.Default
):

  /** Apply settings to a Hermit configuration. */
  def set(config: Configuration): Unit =

    // Translates above enums to respective HermiT settings
    // and applies them to the configuration 'config'.

    directBlocking match
      case HermitDirectBlocking.Single =>
        config.directBlockingType = DirectBlockingType.SINGLE
      case HermitDirectBlocking.PairWise =>
        config.directBlockingType = DirectBlockingType.PAIR_WISE
      case HermitDirectBlocking.Optimal =>
        config.directBlockingType = DirectBlockingType.OPTIMAL
      case HermitDirectBlocking.Default => ()

    blockingStrategy match
      case HermitBlockingStrategy.Anywhere =>
        config.blockingStrategyType = BlockingStrategyType.ANYWHERE
      case HermitBlockingStrategy.Ancestor =>
        config.blockingStrategyType = BlockingStrategyType.ANCESTOR
      case HermitBlockingStrategy.ComplexCore =>
        config.blockingStrategyType = BlockingStrategyType.COMPLEX_CORE
      case HermitBlockingStrategy.SimpleCore =>
        config.blockingStrategyType = BlockingStrategyType.SIMPLE_CORE
      case HermitBlockingStrategy.Optimal =>
        config.blockingStrategyType = BlockingStrategyType.OPTIMAL
      case HermitBlockingStrategy.Default => ()

    signatureCache match
      case HermitSignatureCache.Cached =>
        config.blockingSignatureCacheType = BlockingSignatureCacheType.CACHED
      case HermitSignatureCache.NotCached =>
        config.blockingSignatureCacheType =
          BlockingSignatureCacheType.NOT_CACHED
      case HermitSignatureCache.Default => ()

    existentialStrategy match
      case HermitExistentialStrategy.CreationOrder =>
        config.existentialStrategyType = ExistentialStrategyType.CREATION_ORDER
      case HermitExistentialStrategy.IndividualReuse =>
        config.existentialStrategyType =
          ExistentialStrategyType.INDIVIDUAL_REUSE
      case HermitExistentialStrategy.El =>
        config.existentialStrategyType = ExistentialStrategyType.EL
      case HermitExistentialStrategy.Default => ()
