import { Module } from "@nestjs/common";
import { UserController, ValidationController } from "../controllers";

@Module({
  controllers: [UserController, ValidationController],
})
export class AppModule {}
