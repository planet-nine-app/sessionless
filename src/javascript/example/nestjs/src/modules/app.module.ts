import { Module } from "@nestjs/common";
import { UserController } from "../controllers";

@Module({
  controllers: [UserController],
})
export class AppModule {}
