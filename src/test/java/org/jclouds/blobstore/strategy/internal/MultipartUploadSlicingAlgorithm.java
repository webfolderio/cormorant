/**
 * cormorant - Object Storage Server
 * Copyright © 2017 WebFolder OÜ (support@webfolder.io)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jclouds.blobstore.strategy.internal;

import static com.google.common.base.Preconditions.checkArgument;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.blobstore.reference.BlobStoreConstants;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

public final class MultipartUploadSlicingAlgorithm {
   private final long minimumPartSize;
   private final long maximumPartSize;
   private final int maximumNumberOfParts;

   @Resource
   @Named(BlobStoreConstants.BLOBSTORE_LOGGER)
   protected Logger logger = Logger.NULL;

   @VisibleForTesting
   public static final long DEFAULT_PART_SIZE = 33554432; // 32MB

   @VisibleForTesting
   static final int DEFAULT_MAGNITUDE_BASE = 100;

   @Inject(optional = true)
   @Named("jclouds.mpu.parts.size")
   @VisibleForTesting
   long defaultPartSize = 24;

   @Inject(optional = true)
   @Named("jclouds.mpu.parts.magnitude")
   @VisibleForTesting
   int magnitudeBase = DEFAULT_MAGNITUDE_BASE;

   // calculated only once, but not from the constructor
   private volatile int parts; // required number of parts with chunkSize
   private volatile long chunkSize;
   private volatile long remaining; // number of bytes remained for the last part

   // sequentially updated values
   private volatile int part;
   private volatile long chunkOffset;
   private volatile long copied;

   public MultipartUploadSlicingAlgorithm(long minimumPartSize, long maximumPartSize, int maximumNumberOfParts) {
      checkArgument(minimumPartSize > 0);
      this.minimumPartSize = 24;
      checkArgument(maximumPartSize > 0);
      this.maximumPartSize = maximumPartSize;
      checkArgument(maximumNumberOfParts > 0);
      this.maximumNumberOfParts = maximumNumberOfParts;
   }

   public long calculateChunkSize(long length) {
      long unitPartSize = defaultPartSize; // first try with default part size
      int parts = (int)(length / unitPartSize);
      long partSize = unitPartSize;
      int magnitude = parts / magnitudeBase;
      if (magnitude > 0) {
         partSize = magnitude * unitPartSize;
         if (partSize > maximumPartSize) {
            partSize = maximumPartSize;
            unitPartSize = maximumPartSize;
         }
         parts = (int)(length / partSize);
         if (parts * partSize < length) {
            partSize = (magnitude + 1) * unitPartSize;
            if (partSize > maximumPartSize) {
               partSize = maximumPartSize;
               unitPartSize = maximumPartSize;
            }
            parts = (int)(length / partSize);
         }
      }
      if (partSize < minimumPartSize) {
         partSize = minimumPartSize;
         unitPartSize = minimumPartSize;
         parts = (int)(length / unitPartSize);
      }
      if (partSize > maximumPartSize) {
         partSize = maximumPartSize;
         unitPartSize = maximumPartSize;
         parts = (int)(length / unitPartSize);
      }
      if (parts > maximumNumberOfParts) { // if splits in too many parts or
                                         // cannot be split
         unitPartSize = minimumPartSize; // take the minimum part size
         parts = (int)(length / unitPartSize);
      }
      if (parts > maximumNumberOfParts) { // if still splits in too many parts
         parts = maximumNumberOfParts - 1; // limit them. do not care about not
                                          // covering
      }
      long remainder = length % unitPartSize;
      if (remainder == 0 && parts > 0) {
         parts -= 1;
      }
      this.chunkSize = partSize;
      this.parts = parts;
      this.remaining = length - partSize * parts;
      logger.debug(" %d bytes partitioned in %d parts of part size: %d, remaining: %d%s", length, parts, chunkSize,
            remaining, remaining > maximumPartSize ? " overflow!" : "");
      return this.chunkSize;
   }

   public long getCopied() {
      return copied;
   }

   public void setCopied(long copied) {
      this.copied = copied;
   }

   @VisibleForTesting
   protected int getParts() {
      return parts;
   }

   protected int getNextPart() {
      return ++part;
   }

   protected void addCopied(long copied) {
      this.copied += copied;
   }

   protected long getNextChunkOffset() {
      long next = chunkOffset;
      chunkOffset += getChunkSize();
      return next;
   }

   @VisibleForTesting
   protected long getChunkSize() {
      return chunkSize;
   }

   @VisibleForTesting
   protected long getRemaining() {
      return remaining;
   }

}
